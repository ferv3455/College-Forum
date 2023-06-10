package com.example.myapp.data;

import android.graphics.Bitmap;

public class Like {
    String image;
    String usn;
    String id;

    public Like(String image, String usn,String id) {
        this.image = image;
        this.usn = usn;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
