package com.example.myapp.data;

public class likes {
    int image;
    String usn;

    public likes(int image, String usn) {
        this.image = image;
        this.usn = usn;
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
}
