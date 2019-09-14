package com.sweeky.message.handler.framework.process;

import com.sweeky.message.handler.framework.entity.message.Message;

public interface Processor<T> {
    void process(Message<T> message);
}
