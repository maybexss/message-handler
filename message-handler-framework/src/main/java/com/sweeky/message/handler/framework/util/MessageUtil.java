package com.sweeky.message.handler.framework.util;

import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.entity.file.IndexReadFile;
import com.sweeky.message.handler.framework.entity.file.IndexSaveFile;
import com.sweeky.message.handler.framework.entity.file.MessageReadFile;
import com.sweeky.message.handler.framework.entity.file.MessageSaveFile;
import com.sweeky.message.handler.framework.entity.message.ByteMessage;
import com.sweeky.message.handler.framework.entity.message.IndexMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.sweeky.message.handler.common.constants.DateFormat.DATE_FORMAT_YYYYMMDD;

public class MessageUtil {
    /**
     * file name prefix, file everyday is different
     */
    private static String date = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD).format(new Date());

    /**
     * save index of message
     * file to save index
     */
    private IndexSaveFile indexSaveFile;

    /**
     * file to save message
     */
    private MessageSaveFile messageSaveFile;

    /**
     * file to read index for message
     */
    private IndexReadFile indexReadFile;

    /**
     * file to read message
     */
    private MessageReadFile messageReadFile;

    public MessageUtil(String topicName) {
        MessageConfig messageConfig = SpringContextUtil.getBean(MessageConfig.class);

        messageSaveFile = new MessageSaveFile(messageConfig.getFilePrefixPath(), topicName, date, messageConfig.getFileIncrementSize());
        messageReadFile = new MessageReadFile(messageConfig.getFilePrefixPath(), topicName, date, messageConfig.getFileIncrementSize());

        indexSaveFile = new IndexSaveFile(messageConfig.getFilePrefixPath(), topicName, date, messageConfig.getFileIncrementSize());
        indexReadFile = new IndexReadFile(messageConfig.getFilePrefixPath(), topicName, date, messageConfig.getFileIncrementSize());
    }

    /**
     * save message to file
     *
     * @param data        message data
     * @param messageType message type
     */
    public void saveMessage(byte[] data, int messageType) {
        // save data to message file
        messageSaveFile.saveMessage(data);
        // add message count to message read file
        messageReadFile.addMessageCount();
        // save index to index file
        indexSaveFile.saveIndex(data.length, messageType);
        // add message count to index read file
        indexReadFile.addIndexCount();
    }

    /**
     * read index message
     * return one index message once
     * if there is no more index messages can read, then return null
     *
     * @return one index message
     */
    public IndexMessage readIndexMessage() {
        return indexReadFile.readIndexMessage();
    }

    /**
     * read batch index messages
     * 256 index messages once(32 bytes per index message * 256 = 8KB)
     * if index messages size less than 256, then return the actual size's index messages
     * * if there is no more index messages can read, then return null
     *
     * @return batch index messages
     */
    public List<IndexMessage> readIndexMessageList() {
        return indexReadFile.readIndexMessageList();
    }

    /**
     * read message
     * return one message
     *
     * @param indexMessage message's index
     * @return ByteMessage message
     */
    public ByteMessage readByteMessage(IndexMessage indexMessage) {
        return messageReadFile.readByteMessage(indexMessage);
    }

    /**
     * read messages
     * return 256 messages every time
     * indexMessageList.size is 256
     * when indexMessageList.size is less than 256, then return the current size of message
     *
     * @param indexMessageList index message list
     * @return batch messages
     */
    public List<ByteMessage> readByteMessageList(List<IndexMessage> indexMessageList) {
        return messageReadFile.readByteMessageList(indexMessageList);
    }
}
