package com.example.myapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.activity.SpaceActivity;
import com.example.myapp.data.Follow;
import com.example.myapp.data.FollowList;

class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final ImageView avatarView;
    public final TextView usernameView;
    public final TextView descriptionView;
    private final FollowListAdapter adapter;

    public FollowViewHolder(@NonNull View itemView, FollowListAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);
        avatarView = itemView.findViewById(R.id.follower_avatar);
        usernameView = itemView.findViewById(R.id.follower_name);
        descriptionView = itemView.findViewById(R.id.follower_description);
    }

    @Override
    public void onClick(View view) {
        int position = this.getAdapterPosition();
        String objectUsername = adapter.getUsernameAt(position);
        adapter.showFollowDetail(objectUsername);
    }
}
public class FollowListAdapter extends RecyclerView.Adapter<FollowViewHolder> {
    private final FollowList followList;
    private final LayoutInflater inflater;
    private final Context context;
    private final FollowDetailEventListener listener;

    public interface FollowDetailEventListener {
        void onDisplayFollowDetail(String username);
    }

    public FollowListAdapter(Context context, FollowList followList, FollowDetailEventListener listener) {
        inflater = LayoutInflater.from(context);
        this.followList = followList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view
        View mItemView = inflater.inflate(R.layout.follow_item, parent, false);
        return new FollowViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        Follow follow = followList.get(position);
        // Update other views in the holder based on the Follow object
        byte[] decodedString = Base64.decode(follow.getAvatar(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.avatarView.setImageBitmap(decodedByte);
        holder.usernameView.setText(follow.getFollowerName());
        holder.descriptionView.setText(follow.getDescription());
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public String getUsernameAt(int index) {
        return followList.get(index).getFollowerName();
    }

    public void showFollowDetail(String username) {
        listener.onDisplayFollowDetail(username);
    }
}


