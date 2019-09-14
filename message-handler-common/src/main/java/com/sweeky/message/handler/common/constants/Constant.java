package com.sweeky.message.handler.common.constants;

public class Constant {
    /**
     * message topic
     */
    public static final String MESSAGE_TEST = "TEST";

    /**
     * program is running
     */
    public static boolean IS_RUNNING = true;

    /**
     * post fix of index | message file
     */
    public static final String FILE_POST_FIX = ".bin";

    /**
     * index message size
     * messageId: 8
     * currentTime: 8
     * startOffset: 8
     * dataLength: 4
     * messageType: 4
     */
    public static final int INDEX_MESSAGE_SIZE = 32;

    public static final String CONVERTER = "Converter";

    public static final String PROCESSOR = "Processor";
}
