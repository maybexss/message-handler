package com.sweeky.message.handler.framework.entity.message;

/**
 * message entity class
 */
public class Message<T> {
    private long messageId;

    private MessageHeader header;

    private T body;

    private boolean updateMessage;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(boolean updateMessage) {
        this.updateMessage = updateMessage;
    }
}
