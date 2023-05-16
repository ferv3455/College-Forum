package com.example.myapp.data;

public class replies {
    private int image;
    private String usn;
    private String reply;

    public replies(int image, String usn, String reply) {
        this.image = image;
        this.usn = usn;
        this.reply = reply;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
