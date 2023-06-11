package com.example.myapp.data;

import android.graphics.Bitmap;

public class Like {
    private String image;
    private String usn;
    private String id;
    private String title;
    private String time;

    public Like(String image, String usn, String id, String title, String time) {
        this.image = image;
        this.usn = usn;
        this.id = id;
        this.title = title;
        this.time = time;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
