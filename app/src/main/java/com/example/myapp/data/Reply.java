package com.example.myapp.data;

public class Reply {
    private String image;
    private String usn;
    private String reply;
    private String id;
    private String title;
    private String time;

    public Reply(String image, String usn, String reply, String id, String title, String time) {
        this.image = image;
        this.usn = usn;
        this.reply = reply;
        this.id = id;
        this.title = title;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
