package com.example.messenger.entity;

public class Message {

    private String sender;
    private String reciever;
    private String message;
    private boolean isseen;
    private String date;


    public Message() {
    }

    public Message(String sender, String reciever, String message, boolean isseen, String date) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.isseen = isseen;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
