package com.example.suppileragrimart.model;

public class Chat {
    private String sender;
    private String message;
    private String receiver;
    private boolean isseen;
    private String url;
    private String messageId;

    public Chat() {
    }

    public Chat(String sender, String message, String receiver, boolean isseen, String url, String messageId) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.isseen = isseen;
        this.url = url;
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
