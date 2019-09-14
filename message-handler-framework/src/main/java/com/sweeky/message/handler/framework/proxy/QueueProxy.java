package com.sweeky.message.handler.framework.proxy;

import com.sweeky.message.handler.framework.entity.message.ByteMessage;
import com.sweeky.message.handler.framework.entity.message.Message;
import com.sweeky.message.handler.framework.queue.MessageQueue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueueProxy {

    /**
     * ByteMessage queue
     */
    private static Map<String, MessageQueue<ByteMessage>> cacheQueueMap = new ConcurrentHashMap<>();

    /**
     * Message queue
     */
    private static Map<String, MessageQueue<Message>> messageQueueMap = new ConcurrentHashMap<>();

    public static MessageQueue<ByteMessage> getCacheQueue(String topicName) {
        return cacheQueueMap.computeIfAbsent(topicName, k -> new MessageQueue<>());
    }

    public static MessageQueue<Message> getMessageQueue(String topicName) {
        return messageQueueMap.computeIfAbsent(topicName, k -> new MessageQueue<>());
    }
}
