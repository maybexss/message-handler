package com.sweeky.message.handler.framework.read;

import com.sweeky.common.LogUtil;
import com.sweeky.message.handler.common.constants.Constant;
import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.entity.message.ByteMessage;
import com.sweeky.message.handler.framework.entity.message.IndexMessage;
import com.sweeky.message.handler.framework.proxy.MessageProxy;
import com.sweeky.message.handler.framework.proxy.QueueProxy;
import com.sweeky.message.handler.framework.queue.MessageQueue;
import com.sweeky.message.handler.framework.util.MessageUtil;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ReadByteMessageThread extends Thread {
    private String topicName;

    private MessageQueue<ByteMessage> cacheQueue;

    private MessageUtil messageUtil;

    ReadByteMessageThread(String topicName, MessageConfig config) {
        setName(topicName + "-ReadByteMessageThread");
        this.topicName = topicName;
        this.cacheQueue = QueueProxy.getCacheQueue(topicName);
        this.messageUtil = MessageProxy.getMessageUtil(topicName);
    }

    @Override
    public void run() {
        while (Constant.IS_RUNNING) {
            try {
                readBatchByteMessages();
            } catch (Exception e) {
                LogUtil.error(ReadByteMessageThread.class, getName(), e.getMessage(), e);
            }
        }
    }

    /**
     * read one byte message once
     */
    private void readByteMessage() {
        IndexMessage indexMessage = messageUtil.readIndexMessage();
        if (indexMessage != null) {
            ByteMessage byteMessage = messageUtil.readByteMessage(indexMessage);
            if (byteMessage != null) {
                cacheQueue.put(byteMessage);
            }
        }
    }

    /**
     * read a batch of messages once
     */
    private void readBatchByteMessages() {
        List<IndexMessage> indexMessageList = messageUtil.readIndexMessageList();
        if (!CollectionUtils.isEmpty(indexMessageList)) {
            List<ByteMessage> byteMessageList = messageUtil.readByteMessageList(indexMessageList);
            if (!CollectionUtils.isEmpty(byteMessageList)) {
                cacheQueue.putList(byteMessageList);
            }
        }
    }
}