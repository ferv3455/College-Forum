package com.example.myapp.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Post implements Parcelable {
    private final String id;
    private final String intro;
    private final String content;
    private final String username;
    private final String tag;
    private final String[] images;
    private final String createdAt;
    private int comments;
    private int likes;
    private int stars;

    public Post(String intro, String content, String username, String tag) {
        this.id = "";
        this.intro = intro;
        this.content = content;
        this.username = username;
        this.tag = tag;
        this.createdAt = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
//        this.images = new String[(int)(Math.random() * 9)];
//        for (int i = 0; i < images.length; i++) {
//            images[i] = testImage[(int)(Math.random() * 2)];
//        }
        this.images = new String[0];
        this.comments = (int)(Math.random() * 1000);
        this.likes = (int)(Math.random() * 1000);
        this.stars = (int)(Math.random() * 1000);
    }

    public Post(String intro, String content, String[] images, String username, String tag) {
        this.id = "";
        this.intro = intro;
        this.content = content;
        this.username = username;
        this.tag = tag;
        this.createdAt = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        this.images = images;
        this.comments = (int)(Math.random() * 1000);
        this.likes = (int)(Math.random() * 1000);
        this.stars = (int)(Math.random() * 1000);
    }

    public Post(Parcel in) {
        this.id = in.readString();
        this.intro = in.readString();
        this.content = in.readString();
        this.username = in.readString();
        this.tag = in.readString();
        this.images = in.createStringArray();
        this.createdAt = in.readString();
        this.comments = in.readInt();
        this.likes = in.readInt();
        this.stars = in.readInt();
    }

    public Post(JSONObject obj, boolean full) {
        try {
            this.id = obj.getString("id");
            this.intro = obj.getString("title");
            this.content = obj.getString("content");
            this.username = obj.getJSONObject("user").getString("username");
            this.tag = obj.getJSONArray("tags").getJSONObject(0).getString("name");
            this.createdAt = obj.getString("createdAt");
            this.comments = obj.getInt("comments");
            this.likes = obj.getInt("likes");
            this.stars = obj.getInt("favorites");

            JSONArray image_list = obj.getJSONArray("images");
            int size = image_list.length();
            this.images = new String[size];
            for (int i = 0; i < size; i++) {
                this.images[i] = image_list.getJSONObject(i).getString(full ? "content" : "thumbnail");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return id;
    }

    public String getIntro() {
        return intro;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getTag() {
        return tag;
    }

    public String[] getImages() {
        return images;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getComments() {
        return comments;
    }

    public int getLikes() {
        return likes;
    }

    public int getStars() {
        return stars;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(intro);
        parcel.writeString(content);
        parcel.writeString(username);
        parcel.writeString(tag);
        parcel.writeStringArray(images);
        parcel.writeString(createdAt);
        parcel.writeInt(comments);
        parcel.writeInt(likes);
        parcel.writeInt(stars);
    }

    public static Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
