package com.sweeky.message.handler.framework.entity.file;

import com.sweeky.common.LogUtil;
import com.sweeky.message.handler.framework.generator.MessageIdGenerator;
import com.sweeky.message.handler.framework.util.SpringContextUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

import static com.sweeky.message.handler.common.constants.Constant.FILE_POST_FIX;
import static com.sweeky.message.handler.common.constants.Constant.INDEX_MESSAGE_SIZE;

/**
 * index file to save index
 * use RandomAccessFile
 */
public class IndexSaveFile {
    /**
     * file mode for random access file
     */
    private static final String FILE_MODE = "rw";

    /**
     * extend file size for random access file
     */
    private long fileIncrementSize;

    /**
     * index file
     */
    private File file;

    /**
     * random access file to save index
     */
    private RandomAccessFile randomAccessFile;

    /**
     * current offset the index reached
     */
    private AtomicLong currentOffset = new AtomicLong(0);

    public IndexSaveFile(String fileNamePrefixPath, String topicName, String date, long fileSize) {
        try {
            this.fileIncrementSize = fileSize;
            boolean fileExist = true;
            File file = new File(fileNamePrefixPath.concat(topicName).concat("_").concat(date).concat(FILE_POST_FIX));
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

            this.randomAccessFile = new RandomAccessFile(file, FILE_MODE);
            this.randomAccessFile.setLength(fileExist ? file.length() : fileSize);
            this.randomAccessFile.seek(0L);
        } catch (IOException e) {
            LogUtil.error(IndexSaveFile.class, "constructor", e.getMessage(), e);
        }
    }

    /**
     * save index to file
     *
     * @param dataLength  data.length
     * @param messageType message type
     */
    public void saveIndex(int dataLength, int messageType) {
        try {
            if (currentOffset.get() + INDEX_MESSAGE_SIZE > randomAccessFile.length()) {
                extendIndexSaveFile();
            }

            MessageIdGenerator messageIdGenerator = SpringContextUtil.getBean(MessageIdGenerator.class);

            randomAccessFile.writeLong(messageIdGenerator.getMessageId());
            randomAccessFile.writeLong(System.currentTimeMillis());
            randomAccessFile.writeLong(currentOffset.get());
            randomAccessFile.writeInt(dataLength);
            randomAccessFile.writeInt(messageType);

            currentOffset.addAndGet(INDEX_MESSAGE_SIZE);
        } catch (IOException e) {
            LogUtil.error(IndexSaveFile.class, "saveIndex", e.getMessage(), e);
        }
    }

    /**
     * extend index file, fileIncrementSize bytes add every time
     */
    private void extendIndexSaveFile() throws IOException {
        long oldFileLength = randomAccessFile.length();
        randomAccessFile.getFD().sync();
        randomAccessFile.close();

        randomAccessFile = new RandomAccessFile(file, FILE_MODE);
        randomAccessFile.setLength(oldFileLength + fileIncrementSize);
        randomAccessFile.seek(currentOffset.get());
    }
}
