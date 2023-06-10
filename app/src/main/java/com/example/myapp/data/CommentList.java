package com.example.myapp.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CommentList {
    private ArrayList<Comment> list;

    public CommentList() {
        list = new ArrayList<>();
    }

    public void update(JSONArray array) {
        list.clear();
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Comment(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Comment comment) {
        list.add(comment);
    }

    public void insert(int index, Comment comment) {
        list.add(index, comment);
    }

    public void delete(int index) {
        list.remove(index);
    }

    public Comment get(int index) {
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
        return String.format("<CommentList of length %d>", list.size());
    }
}
