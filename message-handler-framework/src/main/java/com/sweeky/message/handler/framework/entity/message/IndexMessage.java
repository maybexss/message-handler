package com.sweeky.message.handler.framework.entity.message;

/**
 * index entity class
 */
public class IndexMessage {
    /**
     * message id(generated when message reached)
     */
    private long messageId;

    /**
     * message reached time(generated when message reached)
     */
    private long receivedTime;

    /**
     * message offset written on message file
     */
    private long startOffset;

    /**
     * message length, byte array length
     */
    private int dataLength;

    /**
     * message type
     */
    private int messageType;

    public IndexMessage(long messageId, long receivedTime, long startOffset, int dataLength, int messageType) {
        this.messageId = messageId;
        this.receivedTime = receivedTime;
        this.startOffset = startOffset;
        this.dataLength = dataLength;
        this.messageType = messageType;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTimee(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public long getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "IndexMessage{" +
                "messageId=" + messageId +
                ", receivedTime=" + receivedTime +
                ", startOffset=" + startOffset +
                ", dataLength=" + dataLength +
                ", messageType=" + messageType +
                '}';
    }
}
