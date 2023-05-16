package com.example.myapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostList implements Parcelable {
    private ArrayList<Post> list;

    public PostList() {
        list = new ArrayList<>();
    }

    private PostList(Parcel in) {
        list = in.createTypedArrayList(Post.CREATOR);
    }

    public void update(JSONArray array) {
        list.clear();
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Post(array.getJSONObject(i), false));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Post post) {
        list.add(post);
    }

    public void insert(int index, Post post) {
        list.add(index, post);
    }

    public void delete(int index) {
        list.remove(index);
    }

    public Post get(int index) {
        return list.get(index);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public void clear() {
        list.clear();
    }

    @Override
    public String toString() {
        return String.format("<PostList of length %d>", list.size());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(list);
    }

    // Method to recreate a Question from a Parcel
    public static Creator<PostList> CREATOR = new Creator<PostList>() {
        @Override
        public PostList createFromParcel(Parcel source) {
            return new PostList(source);
        }

        @Override
        public PostList[] newArray(int size) {
            return new PostList[size];
        }
    };
}
