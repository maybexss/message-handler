package com.sweeky.message.handler.framework.convert;

import com.sweeky.message.handler.framework.entity.message.ByteMessage;
import com.sweeky.message.handler.framework.entity.message.Message;

public interface Converter<T> {
    Message<T> convert(ByteMessage byteMessage);
}
