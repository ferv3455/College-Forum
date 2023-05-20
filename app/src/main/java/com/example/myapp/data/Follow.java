package com.example.myapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Follow implements Parcelable {
    private final String followerName;
    private final int id;
    private final String avatar;
    private final String description;

    public Follow(int id, String followerName, String avatar, String description) {
        this.id = id;
        this.followerName = followerName;
        this.avatar = avatar;
        this.description = description;
    }

    public Follow(Parcel in) {
        this.id = in.readInt();
        this.followerName = in.readString();
        this.avatar = in.readString();
        this.description = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getFollowerName() {
        return followerName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(followerName);
        parcel.writeString(avatar);
        parcel.writeString(description);
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
