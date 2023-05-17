package com.example.myapp.data;

public class ChatSession {
    int image;
    String usn;
    String message;

    public ChatSession(int image, String usn, String messages) {
        this.image = image;
        this.usn = usn;
        this.message = message;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
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
}
