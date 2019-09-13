package com.sweeky.message.handler.framework.entity.message;

import java.util.Arrays;

/**
 * byte message read from message file
 */
public class ByteMessage {
    /**
     * message id
     */
    private long messageId;

    /**
     * message data, byte array
     */
    private byte[] data;

    /**
     * message received time
     */
    private long receivedTime;

    /**
     * message type
     */
    private int messageType;

    public ByteMessage(long messageId, byte[] data, long receivedTime, int messageType) {
        this.messageId = messageId;
        this.data = data;
        this.receivedTime = receivedTime;
        this.messageType = messageType;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "ByteMessage{" +
                "messageId=" + messageId +
                ", data=" + Arrays.toString(data) +
                ", receivedTime=" + receivedTime +
                ", messageType=" + messageType +
                '}';
    }
}
