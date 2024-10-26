package com.company.plantshop_nguyentiendung_se171710.Model;

public class Message {
    private String senderId;
    private String message;
    private String senderRole;

    public Message() {
    }

    public Message(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }
}
