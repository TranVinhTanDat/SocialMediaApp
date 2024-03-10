package com.team8.socialmedia.models;

import com.google.firebase.database.PropertyName;

public class ModelChat {
    String message, receiver, sender, timestamp, type;
    boolean isSeen;
    public ModelChat() {

    }

    public ModelChat(String message, String receiver, String sender, String timestamp, String type, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.isSeen = isSeen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @PropertyName("message")
    public String getMessage() {
        return message;
    }
    @PropertyName("message")
    public void setMessage(String message) {
        this.message = message;
    }
    @PropertyName("receiver")
    public String getReceiver() {
        return receiver;
    }
    @PropertyName("receiver")
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    @PropertyName("sender")
    public String getSender() {
        return sender;
    }
    @PropertyName("sender")
    public void setSender(String sender) {
        this.sender = sender;
    }
    @PropertyName("timestamp")
    public String getTimeStamp() {
        return timestamp;
    }
    @PropertyName("timestamp")
    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("isSeen")
    public boolean isSeen() {
        return isSeen;
    }

    @PropertyName("isSeen")
    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
