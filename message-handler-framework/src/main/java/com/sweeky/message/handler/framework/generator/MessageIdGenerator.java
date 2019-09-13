package com.sweeky.message.handler.framework.generator;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * used to generate message id
 */
@Service
public class MessageIdGenerator {
    private AtomicLong seed;

    /**
     * generate a new message id
     *
     * @return new message id
     */
    public long getMessageId() {
        if (seed == null) {
            seed = new AtomicLong(0);
        }

        return seed.incrementAndGet();
    }

    /**
     * reset message id when program re-started or master is down and slave is up to master
     *
     * @param maxMessageId current maxMessageId in database
     */
    public void initMessageId(long maxMessageId) {
        if (maxMessageId > 0) {
            seed = new AtomicLong(maxMessageId);
        }
    }
}
