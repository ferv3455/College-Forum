package com.example.myapp.data;

import java.util.List;

public class ChatSession {
    String image;
    String usn;
    String message;
    String time;
    List<Message> chathistory;

    public ChatSession(String image, String usn, String message, String time, List<Message> chathistory) {
        this.image = image;
        this.usn = usn;
        this.message = message;
        this.time = time;
        this.chathistory = chathistory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Message> getChathistory() {
        return chathistory;
    }

    public void setChathistory(List<Message> chathistory) {
        this.chathistory = chathistory;
    }
}
