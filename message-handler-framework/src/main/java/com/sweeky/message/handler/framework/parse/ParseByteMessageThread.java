package com.sweeky.message.handler.framework.parse;

import com.sweeky.common.LogUtil;
import com.sweeky.message.handler.common.constants.Constant;
import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.convert.Converter;
import com.sweeky.message.handler.framework.entity.message.ByteMessage;
import com.sweeky.message.handler.framework.entity.message.Message;
import com.sweeky.message.handler.framework.proxy.QueueProxy;
import com.sweeky.message.handler.framework.queue.MessageQueue;
import com.sweeky.message.handler.framework.util.SpringServiceUtil;

public class ParseByteMessageThread extends Thread {
    private String topicName;

    private MessageConfig config;

    private MessageQueue<ByteMessage> cacheQueue;

    private MessageQueue<Message> messageQueue;

    private SpringServiceUtil serviceUtil;

    ParseByteMessageThread(String topicName, MessageConfig config, SpringServiceUtil serviceUtil) {
        setName(topicName + "-ParseByteMessageThread");
        this.topicName = topicName;
        this.config = config;
        this.cacheQueue = QueueProxy.getCacheQueue(topicName);
        this.messageQueue = QueueProxy.getMessageQueue(topicName);
        this.serviceUtil = serviceUtil;
    }

    @Override
    public void run() {
        while (Constant.IS_RUNNING) {
            try {
                parseByteMessage();
            } catch (Exception e) {
                LogUtil.error(ParseByteMessageThread.class, getName(), e.getMessage(), e);
            }
        }
    }

    /**
     * parse byte message to message
     */
    private void parseByteMessage() {
        ByteMessage byteMessage = cacheQueue.take();

        String converterName = topicName + byteMessage.getMessageId() + Constant.CONVERTER;
        Converter converter = serviceUtil.getConverter(converterName);
        if (converter != null) {
            Message message = converter.convert(byteMessage);
            if (message != null) {
                messageQueue.put(message);
            }
        } else {
            LogUtil.error(ParseByteMessageThread.class, getName(), "no converter can use for {}", converterName);
        }
    }
}
