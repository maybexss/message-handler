package com.sweeky.message.handler.framework.handler;

import com.sweeky.message.handler.common.constants.Constant;
import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.entity.message.ReceivedMessage;
import com.sweeky.message.handler.framework.proxy.MessageProxy;
import com.sweeky.message.handler.framework.topic.Topic;
import com.sweeky.message.handler.framework.util.ExecutorServiceUtil;
import com.sweeky.message.handler.framework.util.SpringContextUtil;
import com.sweeky.message.handler.logger.LogUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * handle received message
 */
public abstract class MessageHandler {
    /**
     * count messages received
     */
    private static AtomicLong messageReceived = new AtomicLong(0);

    public MessageHandler() {
        initMonitorMessageReceivedIndicatorTask();
    }

    /**
     * deal with message when message reached, sub clazz must extend this method and implement it
     *
     * @param message received message
     */
    abstract protected void onMessageReceived(ReceivedMessage message);

    /**
     * sub clazz dispatch this method to save message to file
     *
     * @param data        byte array message data
     * @param messageType message type
     * @param topic       topic, for different messages
     */
    protected void save(byte[] data, int messageType, Topic topic) {
        indicator();
//        logMessage(data, messageType, topic);
        saveMessage(data, messageType, topic);
    }

    /**
     * count message received
     */
    private void indicator() {
        messageReceived.incrementAndGet();
    }

    /**
     * log received message
     *
     * @param data        byte array message data
     * @param messageType message type
     * @param topic       topic
     */
    private void logMessage(byte[] data, int messageType, Topic topic) {
        if (LogUtil.isDebugEnabled(MessageHandler.class)) {
            LogUtil.debug(MessageHandler.class, "logMessage", "topic:{}, messageType: {}, data: {}", topic.getTopicName(), messageType,
                    Arrays.toString(data));
        }
    }

    /**
     * save message to file
     *
     * @param data        byte array data
     * @param messageType message type
     * @param topic       topic
     */
    private void saveMessage(byte[] data, int messageType, Topic topic) {
        if (StringUtils.isEmpty(topic.getTopicName())) {
            return;
        }

        MessageProxy.getMessageUtil(topic.getTopicName()).saveMessage(data, messageType);
    }

    /**
     * monitor message receive rate
     */
    private void initMonitorMessageReceivedIndicatorTask() {
        String threadName = "messageReceivedMonitorThread";
        MessageConfig messageConfig = SpringContextUtil.getBean(MessageConfig.class);

        if (messageConfig.getLogMessageReceivedScheduleTime() > 0) {
            ExecutorServiceUtil.generateSingleExecutorService(threadName, true).submit(() -> {
                long messageCount = messageReceived.get();
                long startTime = System.currentTimeMillis();
                while (Constant.IS_RUNNING) {
                    try {
                        TimeUnit.SECONDS.sleep(messageConfig.getLogMessageReceivedScheduleTime());
                        double v = (messageReceived.get() - messageCount) * 1.0D / (System.currentTimeMillis() - startTime) * 1_000;
                        LogUtil.info(MessageHandler.class, threadName, "message received rate: {} per second", v);
                    } catch (Exception e) {
                        LogUtil.error(MessageHandler.class, threadName, e.getMessage(), e);
                    }
                }
            });
        }
    }
}