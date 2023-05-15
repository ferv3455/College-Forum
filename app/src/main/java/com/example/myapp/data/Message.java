package com.example.myapp.data;

public class Message {
    public final boolean incoming;
    public final String content;

    public Message(String content, boolean incoming) {
        this.content = content;
        this.incoming = incoming;
    }
}
