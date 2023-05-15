package com.example.myapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Follow implements Parcelable {
    private final String followerName;

    public Follow(String followerName) {
        this.followerName = followerName;
    }

    public Follow(Parcel in) {
        this.followerName = in.readString();
    }

    public String getFollowerName() {
        return followerName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(followerName);
    }

    public static Creator<Follow> CREATOR = new Creator<Follow>() {
        @Override
        public Follow createFromParcel(Parcel source) {
            return new Follow(source);
        }

        @Override
        public Follow[] newArray(int size) {
            return new Follow[size];
        }
    };
}

