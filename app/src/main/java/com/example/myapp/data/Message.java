package com.example.myapp.data;

public class Message {
    public final boolean incoming;
    public final String content;
    public final String time;

    public boolean isIncoming() {
        return incoming;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public Message(String content, boolean incoming, String time) {
        this.content = content;
        this.incoming = incoming;
        this.time = time;
    }
}
