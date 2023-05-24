package com.example.myapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Follow implements Parcelable {
    private final String followerName;
    private final int id;
    private final String avatar;
    private final String description;
    private boolean isFollowed;

    public Follow(int id, String followerName, String avatar, String description, boolean isFollowed) {  // 新增参数
        this.id = id;
        this.followerName = followerName;
        this.avatar = avatar;
        this.description = description;
        this.isFollowed = isFollowed;
    }

    public Follow(Parcel in) {
        this.id = in.readInt();
        this.followerName = in.readString();
        this.avatar = in.readString();
        this.description = in.readString();
        this.isFollowed = in.readByte() != 0;
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

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
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
        parcel.writeByte((byte) (isFollowed ? 1 : 0));
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
