package com.sweeky.message.handler.framework.entity.file;

import com.sweeky.common.LogUtil;
import com.sweeky.message.handler.framework.entity.message.IndexMessage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.sweeky.message.handler.common.constants.Constant.FILE_POST_FIX;
import static com.sweeky.message.handler.common.constants.Constant.INDEX_MESSAGE_SIZE;

/**
 * file to read index
 * use MappedByteBuffer
 */
public class IndexReadFile {
    /**
     * file mode for random access file
     */
    private static final String FILE_MODE = "rw";

    /**
     * batch size of index messages
     */
    private static final int BATCH_SIZE = 256;

    /**
     * re-map file size for mapped byte buffer
     */
    private long fileIncrementSize;

    /**
     * random access file to access file or re-map
     */
    private RandomAccessFile randomAccessFile;

    /**
     * mapped byte buffer, map file to ram to fast access file
     */
    private MappedByteBuffer indexReadMappedByteBuffer;

    /**
     * current mapped file length, increase when first map and re-map
     */
    private AtomicLong mappedLength;

    /**
     * current read index offset
     */
    private AtomicLong currentOffset = new AtomicLong(0);

    /**
     * current read index messages
     */
    private AtomicLong currentIndexCount = new AtomicLong(0);

    /**
     * total index count of index messages
     */
    private AtomicLong totalIndexCount = new AtomicLong(0);

    public IndexReadFile(String filePrefixPath, String topicName, String date, long fileLength) {
        try {
            this.fileIncrementSize = fileLength;
            boolean fileExist = true;
            File file = new File(filePrefixPath.concat(topicName).concat("_").concat(date).concat(FILE_POST_FIX));
            if (!file.getParentFile().exists()) {
                fileExist = false;
                if (!file.getParentFile().mkdirs()) {
                    throw new IOException("mkdir failed: " + file.getParentFile().getAbsolutePath());
                }
            }

            if (!file.exists()) {
                fileExist = false;
                if (!file.createNewFile()) {
                    throw new IOException("create file failed: " + file.getName());
                }
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, FILE_MODE);
            randomAccessFile.setLength(fileExist ? file.length() : fileLength);
            this.randomAccessFile = randomAccessFile;

            this.indexReadMappedByteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, 0, fileLength);

            this.mappedLength = new AtomicLong(fileLength);
        } catch (IOException e) {
            LogUtil.error(IndexReadFile.class, "constructor", e.getMessage(), e);
        }
    }

    /**
     * total index count add one
     */
    public void addIndexCount() {
        totalIndexCount.incrementAndGet();
    }

    /**
     * return one index message per time
     * f there is no index message, return null
     *
     * @return index message
     */
    public IndexMessage readIndexMessage() {
        if (currentIndexCount.get() >= totalIndexCount.get()) {
            return null;
        }

        try {
            if (currentOffset.get() + INDEX_MESSAGE_SIZE > mappedLength.get()) {
                reMapIndexReadMappedByteBuffer();
            }

            byte[] buffer = new byte[INDEX_MESSAGE_SIZE];
            indexReadMappedByteBuffer.get(buffer);

            currentOffset.addAndGet(INDEX_MESSAGE_SIZE);
            currentIndexCount.incrementAndGet();

            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            return new IndexMessage(byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getInt(), byteBuffer.getInt());
        } catch (IOException e) {
            LogUtil.error(IndexReadFile.class, "readIndexMessage", e.getMessage(), e);
        }

        return null;
    }

    /**
     * return 256 index messages per time
     * if index messages is less than 256, then return the actual size's index message
     * if there is no index message, return null
     *
     * @return batch index messages
     */
    public List<IndexMessage> readIndexMessageList() {
        if (currentIndexCount.get() >= totalIndexCount.get()) {
            return null;
        }

        try {
            if (currentOffset.get() + INDEX_MESSAGE_SIZE * BATCH_SIZE > mappedLength.get()) {
                reMapIndexReadMappedByteBuffer();
            }

            int size = BATCH_SIZE;
            if (currentIndexCount.get() + BATCH_SIZE > totalIndexCount.get()) {
                size = (int) (totalIndexCount.get() - currentIndexCount.get());
            }

            byte[] buffer = new byte[INDEX_MESSAGE_SIZE * size];
            indexReadMappedByteBuffer.get(buffer);

            currentOffset.addAndGet(INDEX_MESSAGE_SIZE * size);
            currentIndexCount.addAndGet(size);

            List<IndexMessage> indexMessageList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, i * INDEX_MESSAGE_SIZE, INDEX_MESSAGE_SIZE);
                IndexMessage indexMessage = new IndexMessage(byteBuffer.getLong(), byteBuffer.getLong(), byteBuffer.getLong(),
                        byteBuffer.getInt(), byteBuffer.getInt());

                indexMessageList.add(indexMessage);
            }

            return indexMessageList;
        } catch (IOException e) {
            LogUtil.error(IndexReadFile.class, "readIndexMessageList", e.getMessage(), e);
        }

        return null;
    }

    /**
     * re-map index read mapped byte buffer when reached or will reach soon the end of current mapped byte buffer
     * re-map fileIncrementSize once or mapped to the end of file if reached the end of file
     *
     * @throws IOException ioException
     */
    private void reMapIndexReadMappedByteBuffer() throws IOException {
        if (mappedLength.get() + fileIncrementSize <= randomAccessFile.length()) {
            indexReadMappedByteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, currentOffset.get(), fileIncrementSize);
            mappedLength.addAndGet(fileIncrementSize);
        } else {
            long deltaFileLength = randomAccessFile.length() - currentOffset.get();
            indexReadMappedByteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, currentOffset.get(), deltaFileLength);
            mappedLength.addAndGet(deltaFileLength);
        }
    }
}
