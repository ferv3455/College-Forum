package com.example.myapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class FollowList implements Parcelable {
    private ArrayList<Follow> list;

    public FollowList() {
        list = new ArrayList<>();
    }

    private FollowList(Parcel in) {
        list = in.createTypedArrayList(Follow.CREATOR);
    }

    public void insert(Follow follow) {
        list.add(follow);
    }

    public void insert(int index, Follow follow) {
        list.add(index, follow);
    }

    public void delete(int index) {
        list.remove(index);
    }

    public Follow get(int index) {
        return list.get(index);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return String.format("<FollowList of length %d>", list.size());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(list);
    }

    public static Creator<FollowList> CREATOR = new Creator<FollowList>() {
        @Override
        public FollowList createFromParcel(Parcel source) {
            return new FollowList(source);
        }

        @Override
        public FollowList[] newArray(int size) {
            return new FollowList[size];
        }
    };

    public void clear() {
        list.clear();
    }

    public void addAll(FollowList other) {
        this.list.addAll(other.getList());
    }

    public ArrayList<Follow> getList() {
        return list;
    }
}
