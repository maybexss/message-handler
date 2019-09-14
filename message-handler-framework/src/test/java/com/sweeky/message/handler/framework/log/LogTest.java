package com.sweeky.message.handler.framework.log;

import com.sweeky.common.LogUtil;

public class LogTest {
    public static void main(String[] args) {
        byte[] data = new byte[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        LogUtil.debug(LogTest.class, "main", "topic: {}, messageType: {}, data: {}", "TEST", 100101, data);
    }
}
