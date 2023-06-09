package com.example.myapp.data;

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
    private final String avatar;
    private final String username;
    private final String[] tags;
    private final String[] images;
    private final String createdAt;
    private final String location;
    private int comments;
    private int likes;
    private int stars;
    private boolean isLiked;
    private boolean isStarred;

    public Post(String intro, String content, String username, String[] tags) {
        this.id = "";
        this.intro = intro;
        this.content = content;
        this.avatar = "";
        this.username = username;
        this.tags = tags;
        this.createdAt = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
//        this.images = new String[(int)(Math.random() * 9)];
//        for (int i = 0; i < images.length; i++) {
//            images[i] = testImage[(int)(Math.random() * 2)];
//        }
        this.images = new String[0];
        this.location = null;
        this.comments = (int)(Math.random() * 1000);
        this.likes = (int)(Math.random() * 1000);
        this.stars = (int)(Math.random() * 1000);
        this.isLiked = false;
        this.isStarred = false;
    }

    public Post(String intro, String content, String[] images, String username, String[] tags) {
        this.id = "";
        this.intro = intro;
        this.content = content;
        this.avatar = "";
        this.username = username;
        this.tags = tags;
        this.createdAt = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        this.images = images;
        this.location = null;
        this.comments = (int)(Math.random() * 1000);
        this.likes = (int)(Math.random() * 1000);
        this.stars = (int)(Math.random() * 1000);
        this.isLiked = false;
        this.isStarred = false;
    }

    public Post(Parcel in) {
        this.id = in.readString();
        this.intro = in.readString();
        this.content = in.readString();
        this.avatar = in.readString();
        this.username = in.readString();
        this.tags = in.createStringArray();
        this.images = in.createStringArray();
        this.createdAt = in.readString();
        this.location = in.readString();
        this.comments = in.readInt();
        this.likes = in.readInt();
        this.stars = in.readInt();
        this.isLiked = in.readInt() > 0;
        this.isStarred = in.readInt() > 0;
    }

    public Post(JSONObject obj, boolean full) {
        try {
            this.id = obj.getString("id");
            this.intro = obj.getString("title");
            this.content = obj.getString("content");
            this.avatar = obj.getJSONObject("user_profile").getString("avatar");
            this.username = obj.getJSONObject("user_profile").getJSONObject("user").getString("username");
            this.createdAt = obj.getString("createdAt");
            this.comments = obj.getInt("comments");
            this.likes = obj.getInt("likes");
            this.stars = obj.getInt("favorites");
            this.isLiked = obj.getBoolean("isLiked");
            this.isStarred = obj.getBoolean("isStarred");

            if (obj.isNull("location")) {
                this.location = null;
            }
            else {
                this.location = obj.getString("location");
            }

            JSONArray image_list = obj.getJSONArray("images");
            int size = image_list.length();
            this.images = new String[size];
            for (int i = 0; i < size; i++) {
                this.images[i] = image_list.getJSONObject(i).getString(full ? "content" : "thumbnail");
            }

            JSONArray tags_list = obj.getJSONArray("tags");
            int t_size = tags_list.length();
            this.tags = new String[t_size];
            for (int i = 0; i < t_size; i++) {
                this.tags[i] = tags_list.getJSONObject(i).getString("name");
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

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getImages() {
        return images;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLocation() {
        return location;
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

    public boolean getIsLiked() {
        return this.isLiked;
    }

    public boolean getIsStarred() {
        return this.isStarred;
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

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public void setIsStarred(boolean isStarred) {
        this.isStarred = isStarred;
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
        parcel.writeString(avatar);
        parcel.writeString(username);
        parcel.writeStringArray(tags);
        parcel.writeStringArray(images);
        parcel.writeString(createdAt);
        parcel.writeString(location);
        parcel.writeInt(comments);
        parcel.writeInt(likes);
        parcel.writeInt(stars);
        parcel.writeInt(isLiked ? 1 : 0);
        parcel.writeInt(isStarred ? 1 : 0);
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
