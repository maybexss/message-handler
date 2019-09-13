package com.sweeky.message.handler.framework.proxy;

import com.sweeky.message.handler.framework.util.MessageUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * proxy to get message util
 */
public class MessageProxy {
    private static Map<String, MessageUtil> messageUtilMap = new ConcurrentHashMap<>();

    /**
     * return message util if map contains topic
     * if not exists topicName, then create one
     *
     * @param topicName topic name
     * @return messageUtil
     */
    public static MessageUtil getMessageUtil(String topicName) {
        return messageUtilMap.computeIfAbsent(topicName, k -> new MessageUtil(topicName));
    }
}
