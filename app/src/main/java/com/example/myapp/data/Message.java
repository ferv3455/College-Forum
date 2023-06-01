package com.example.myapp.data;

public class Message {
    public final boolean incoming;
    public final String content;

    public boolean isIncoming() {
        return incoming;
    }

    public String getContent() {
        return content;
    }

    public Message(String content, boolean incoming) {
        this.content = content;
        this.incoming = incoming;
    }
}
