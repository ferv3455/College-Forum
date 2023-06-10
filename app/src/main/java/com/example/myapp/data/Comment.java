package com.example.myapp.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Comment {
    private final String id;
    private final String content;
    private final String avatar;
    private final String username;
    private final String createdAt;
    private int likes;
    private boolean isLiked;

    public Comment(JSONObject obj) {
        try {
            this.id = obj.getString("id");
            this.content = obj.getString("content");
            this.avatar = obj.getJSONObject("user_profile").getString("avatar");
            this.username = obj.getJSONObject("user_profile").getJSONObject("user").getString("username");
            this.createdAt = obj.getString("createdAt");
            this.likes = obj.getInt("likes");
            this.isLiked = false;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId() {
        return id;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean getIsLiked() {
        return this.isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
