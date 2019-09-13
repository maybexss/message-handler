package com.sweeky.message.handler.framework.entity.file;

import com.sweeky.message.handler.framework.entity.message.ByteMessage;
import com.sweeky.message.handler.framework.entity.message.IndexMessage;
import com.sweeky.message.handler.logger.LogUtil;

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

/**
 * file to read message
 * use MappedByteBuffer
 */
public class MessageReadFile {
    private static final String FILE_MODE = "rw";
    /**
     * re-map file size for mapped byte buffer
     */
    private long fileIncrementSize;

    /**
     * random access file for re-map
     */
    private RandomAccessFile randomAccessFile;

    /**
     * mapped byte buffer, map file to ram to fast access file
     */
    private MappedByteBuffer messageReadByteBuffer;

    /**
     * total message count
     */
    private AtomicLong totalMessageCount = new AtomicLong(0);

    /**
     * current accessed message count
     */
    private AtomicLong currentMessageCount = new AtomicLong(0);

    /**
     * current accessed message offset
     */
    private AtomicLong currentOffset = new AtomicLong(0);

    /**
     * current mapped file length
     */
    private AtomicLong mappedLength;

    public MessageReadFile(String filePrefixPath, String topicName, String date, long fileLength) {
        try {
            fileIncrementSize = fileLength;
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

            this.messageReadByteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, 0, fileLength);

            this.mappedLength = new AtomicLong(fileLength);
        } catch (IOException e) {
            LogUtil.error(MessageReadFile.class, "constructor", e.getMessage(), e);
        }
    }

    /**
     * total message count add one
     */
    public void addMessageCount() {
        totalMessageCount.incrementAndGet();
    }

    /**
     * use index message to read and return message
     *
     * @param indexMessage index message
     * @return ByteMessage message
     */
    public ByteMessage readByteMessage(IndexMessage indexMessage) {
        if (currentMessageCount.get() >= totalMessageCount.get()) {
            return null;
        }

        try {
            if (currentOffset.get() + indexMessage.getDataLength() > mappedLength.get()) {
                reMapMessageReadMappedByteBuffer();
            }

            byte[] buffer = new byte[indexMessage.getDataLength()];
            messageReadByteBuffer.get(buffer);

            currentOffset.addAndGet(indexMessage.getDataLength());
            currentMessageCount.incrementAndGet();

            return new ByteMessage(indexMessage.getMessageId(), buffer, indexMessage.getReceivedTime(), indexMessage.getMessageType());
        } catch (IOException e) {
            LogUtil.error(MessageReadFile.class, "reMapMessageReadMappedByteBuffer", e.getMessage(), e);
        }

        return null;
    }

    /**
     * return a batch of messages
     *
     * @param indexMessageList index messages
     * @return batch ByteMessage messages
     */
    public List<ByteMessage> readByteMessageList(List<IndexMessage> indexMessageList) {
        if (currentMessageCount.get() >= totalMessageCount.get()) {
            return null;
        }

        try {
            IndexMessage firstIndexMessage = indexMessageList.get(0);
            IndexMessage lastIndexMessage = indexMessageList.get(indexMessageList.size() - 1);
            if (lastIndexMessage.getStartOffset() + lastIndexMessage.getDataLength() > mappedLength.get()) {
                reMapMessageReadMappedByteBuffer();
            }

            int bufferLength = (int) (lastIndexMessage.getStartOffset() + lastIndexMessage.getDataLength() - firstIndexMessage.getStartOffset());
            byte[] buffer = new byte[bufferLength];

            messageReadByteBuffer.get(buffer);

            List<ByteMessage> byteMessageList = new ArrayList<>();
            int deltaOffset = 0;
            for (IndexMessage indexMessage : indexMessageList) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, deltaOffset, indexMessage.getDataLength());

                ByteMessage byteMessage = new ByteMessage(indexMessage.getMessageId(), byteBuffer.array(), indexMessage.getReceivedTime(),
                        indexMessage.getMessageType());

                byteMessageList.add(byteMessage);

                deltaOffset += indexMessage.getDataLength();
            }

            currentOffset.addAndGet(bufferLength);
            currentMessageCount.addAndGet(indexMessageList.size());

            return byteMessageList;
        } catch (IOException e) {
            LogUtil.error(MessageReadFile.class, "readByteMessageList", e.getMessage(), e);
        }

        return null;
    }

    /**
     * re-map message read mapped byte buffer
     *
     * @throws IOException ioException
     */
    private void reMapMessageReadMappedByteBuffer() throws IOException {
        if (currentOffset.get() + fileIncrementSize <= randomAccessFile.length()) {
            messageReadByteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, currentOffset.get(), fileIncrementSize);
            mappedLength.addAndGet(fileIncrementSize);
        } else {
            long deltaMappedLength = randomAccessFile.length() - currentOffset.get();
            messageReadByteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_WRITE, currentOffset.get(), deltaMappedLength);
            mappedLength.addAndGet(deltaMappedLength);
        }
    }
}
