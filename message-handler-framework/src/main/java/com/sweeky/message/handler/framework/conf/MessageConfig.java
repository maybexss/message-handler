package com.sweeky.message.handler.framework.conf;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "conf.message")
public class MessageConfig {
    /**
     * scheduled time for logging the rate o received message(default 10 seconds)
     */
    private long logMessageReceivedScheduleTime = 10L;

    /**
     * file prefix path(default ~/MESSAGE_CACHE/)
     */
    private String filePrefixPath = SystemUtils.USER_HOME + File.separator + "MESSAGE_CACHE" + File.separator;

    /**
     * file init size or file increment size to save message or index, or mapped byte buffer size
     */
    private long fileIncrementSize = 128 * 1024 * 1024L;

    /**
     * message queue capacity
     */
    private int messageQueueCapacity = 50_000;

    private String batchPersistentSize = "{\"TEST\":100000}";

    public long getLogMessageReceivedScheduleTime() {
        return logMessageReceivedScheduleTime;
    }

    public void setLogMessageReceivedScheduleTime(long logMessageReceivedScheduleTime) {
        this.logMessageReceivedScheduleTime = logMessageReceivedScheduleTime;
    }

    public long getFileIncrementSize() {
        return fileIncrementSize;
    }

    public void setFileIncrementSize(long fileIncrementSize) {
        this.fileIncrementSize = fileIncrementSize;
    }

    public String getFilePrefixPath() {
        if (!filePrefixPath.endsWith(File.separator)) {
            filePrefixPath = filePrefixPath.concat(File.separator);
        }
        return filePrefixPath;
    }

    public void setFilePrefixPath(String filePrefixPath) {
        this.filePrefixPath = filePrefixPath;
    }

    public int getMessageQueueCapacity() {
        return messageQueueCapacity;
    }

    public void setMessageQueueCapacity(int messageQueueCapacity) {
        this.messageQueueCapacity = messageQueueCapacity;
    }


    public String getBatchPersistentSize() {
        return batchPersistentSize;
    }

    public void setBatchPersistentSize(String batchPersistentSize) {
        this.batchPersistentSize = batchPersistentSize;
    }
}
