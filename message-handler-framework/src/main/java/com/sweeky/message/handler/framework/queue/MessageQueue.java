package com.sweeky.message.handler.framework.queue;

import com.sweeky.common.LogUtil;
import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.util.SpringContextUtil;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageQueue<T> {
    private LinkedBlockingQueue<T> queue;

    public MessageQueue() {
        MessageConfig config = SpringContextUtil.getBean(MessageConfig.class);

        this.queue = new LinkedBlockingQueue<>(config.getMessageQueueCapacity());
    }

    public MessageQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * put message
     *
     * @param t t
     */
    public void put(T t) {
        try {
            queue.put(t);
        } catch (InterruptedException e) {
            LogUtil.error(MessageQueue.class, "put", e.getMessage(), e);
        }
    }

    /**
     * put a batch of messages
     *
     * @param list list with messages, make sure that list is not null and list is not empty before dispatching this method
     */
    public void putList(List<T> list) {
        try {
            for (T t : list) {
                queue.put(t);
            }
        } catch (InterruptedException e) {
            LogUtil.error(MessageQueue.class, "putList", e.getMessage(), e);
        }
    }

    /**
     * take message from queue, it will block until queue is not empty
     *
     * @return message
     */
    public T take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            LogUtil.error(MessageQueue.class, "take", e.getMessage(), e);
        }

        return null;
    }

    /**
     * poll message from queue, it will return right now if queue is empty
     *
     * @return message
     */
    public T poll() {
        return queue.poll();
    }

    /**
     * poll message from queue with wait timeout, it will return right now if queue is not empty, or wait until timeout
     *
     * @param timeout wait timeout
     * @param unit    unit of timeout
     * @return message
     */
    public T poll(long timeout, TimeUnit unit) {
        try {
            return queue.poll(timeout, unit);
        } catch (InterruptedException e) {
            LogUtil.error(MessageQueue.class, "poll with timeout", e.getMessage(), e);
        }

        return null;
    }
}
