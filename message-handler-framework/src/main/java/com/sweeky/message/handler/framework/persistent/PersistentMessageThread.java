package com.sweeky.message.handler.framework.persistent;

import com.alibaba.fastjson.JSONObject;
import com.sweeky.common.LogUtil;
import com.sweeky.message.handler.common.constants.Constant;
import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.entity.message.Message;
import com.sweeky.message.handler.framework.proxy.QueueProxy;
import com.sweeky.message.handler.framework.queue.MessageQueue;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PersistentMessageThread extends Thread {
    private String topicName;

    private MessageConfig config;

    private MessageQueue<Message> messageQueue;

    PersistentMessageThread(String topicName, MessageConfig config) {
        setName(topicName + "-PersistentMessageThread");
        this.topicName = topicName;
        this.config = config;
        this.messageQueue = QueueProxy.getMessageQueue(topicName);
    }

    @Override
    public void run() {
        int batchPersistentSize = parseBatchPersistentSize();
        while (Constant.IS_RUNNING) {
            try {
                persistentMessage(batchPersistentSize);
            } catch (Exception e) {
                LogUtil.error(PersistentMessageThread.class, getName(), e.getMessage(), e);
            }
        }
    }

    /**
     * batch persistent if message list is more than batch persistent size
     * if there is no more message, and it will batch save after 1 second latter
     *
     * @param batchPersistentSize batch persistent size
     */
    private void persistentMessage(int batchPersistentSize) {
        List<Message> messageList = new ArrayList<>();
        while (Constant.IS_RUNNING) {
            Message message = messageQueue.poll(1, TimeUnit.SECONDS);
            if (message == null) {
                if (!CollectionUtils.isEmpty(messageList)) {
                    batchPersistent(messageList);
                }
            } else {
                messageList.add(message);
                if (messageList.size() >= batchPersistentSize) {
                    batchPersistent(messageList);
                }
            }
        }

    }

    private void batchPersistent(List<Message> messageList) {
        // todo
    }

    /**
     * get batch persistent size, parse json and use topicName to get batch persistent size
     *
     * @return batch persistent size
     */
    private int parseBatchPersistentSize() {
        JSONObject jsonObject = JSONObject.parseObject(config.getBatchPersistentSize());

        return Integer.parseInt(String.valueOf(jsonObject.getOrDefault(topicName, "100000")));
    }
}
