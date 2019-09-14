package com.sweeky.message.handler.framework.entity.file;


import com.sweeky.common.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

import static com.sweeky.message.handler.common.constants.Constant.FILE_POST_FIX;

/**
 * file to save message
 * use MappedByteBuffer
 */
public class MessageSaveFile {
    /**
     * file mode for random access file
     */
    private static final String FILE_MODE = "rw";

    /**
     * re-map file size for mapped byte buffer
     */
    private long fileIncrementSize;

    /**
     * message file
     */
    private File file;

    /**
     * random access file for re-map and extend file size
     */
    private RandomAccessFile randomAccessFile;

    /**
     * message mapped byte buffer to fast access file to save message
     */
    private MappedByteBuffer messageSaveMappedByteBuffer;

    /**
     * current offset of message written
     */
    private AtomicLong currentOffset = new AtomicLong(0);

    /**
     * current mapped file length
     */
    private AtomicLong mappedLength;

    public MessageSaveFile(String filePrefixPath, String topicName, String date, long fileLength) {
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

            this.file = file;

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, FILE_MODE);
            randomAccessFile.setLength(fileExist ? file.length() : fileLength);
            this.randomAccessFile = randomAccessFile;

            this.messageSaveMappedByteBuffer = this.randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, 0, fileLength);

            this.mappedLength = new AtomicLong(fileLength);
        } catch (IOException e) {
            LogUtil.error(MessageSaveFile.class, "constructor", e.getMessage(), e);
        }
    }

    /**
     * save message
     *
     * @param data byte array data
     */
    public void saveMessage(byte[] data) {
        try {
            if (currentOffset.get() + data.length > mappedLength.get()) {
                reMapMessageSaveMappedByteBuffer();
            }

            messageSaveMappedByteBuffer.put(data);
            currentOffset.addAndGet(data.length);
        } catch (IOException e) {
            LogUtil.error(MessageSaveFile.class, "saveMessage", e.getMessage(), e);
        }
    }

    /**
     * re-map buffer, when new come message's buffer index is greater than current mapped buffer
     * fileIncrementLength will re-mapped, new mapped length = randomAccessFile.length() - currentOffset
     */
    private void reMapMessageSaveMappedByteBuffer() throws IOException {
        long oldFileLength = randomAccessFile.length();
        messageSaveMappedByteBuffer.force();
        randomAccessFile.getChannel().close();
        randomAccessFile.close();

        randomAccessFile = new RandomAccessFile(file, FILE_MODE);
        randomAccessFile.setLength(oldFileLength + fileIncrementSize);
        randomAccessFile.seek(currentOffset.get());

        long deltaMappedLength = randomAccessFile.length() - currentOffset.get();
        messageSaveMappedByteBuffer = randomAccessFile.getChannel()
                .map(FileChannel.MapMode.READ_WRITE, currentOffset.get(), deltaMappedLength);

        mappedLength.addAndGet(deltaMappedLength);
    }
}
