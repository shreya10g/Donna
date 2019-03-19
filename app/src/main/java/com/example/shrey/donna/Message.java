package com.example.shrey.donna;

public class Message {
    String text;
    boolean user;

    public Message(String text, boolean user){
        this.text = text;
        this.user = user;
    }

    public String getText(){
        return this.text;
    }

    public boolean getUser(){
        return this.user;
    }
}
