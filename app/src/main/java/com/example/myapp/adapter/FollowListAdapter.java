package com.example.myapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.activity.SpaceActivity;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Follow;
import com.example.myapp.data.FollowList;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    public void updateData(FollowList newFollowList) {
        this.followList.clear();
        this.followList.addAll(newFollowList);  // Assuming there is a getList() method in FollowList
        notifyDataSetChanged();
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

        SharedPreferences prefs = context.getSharedPreferences("FollowListPrefs", Context.MODE_PRIVATE);
        Set<String> followingUsernames = prefs.getStringSet("followingList", new HashSet<>());
        String currentUsername = follow.getFollowerName();
        boolean isFollowing = followingUsernames.contains(currentUsername);

        Button followButton = holder.itemView.findViewById(R.id.follow_button);
        followButton.setText(isFollowing ? "取消关注" : "关注");
        followButton.setBackgroundColor(isFollowing ? Color.GRAY : Color.BLUE);  // replace with your desired colors

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentToken = TokenManager.getSavedToken(context);

                // 创建 JSON body
                JSONObject jsonBody = new JSONObject();
                try {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(currentUsername);
                    jsonBody.put("username", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        // Handle the error
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                followButton.setText(isFollowing ? "关注" : "取消关注");
                                followButton.setBackgroundColor(isFollowing ? Color.BLUE : Color.GRAY);  // replace with your desired colors

                                // Update SharedPreferences
                                if (isFollowing) {
                                    followingUsernames.remove(currentUsername);
                                } else {
                                    followingUsernames.add(currentUsername);
                                }
                                prefs.edit().putStringSet("following", followingUsernames).apply();
                            });
                        }
                    }
                    private void runOnUiThread(Object following) {
                    }
                };

                if(isFollowing) {
                    HTTPRequest.delete("account/following", jsonBody.toString(), currentToken, callback);
                } else {
                    HTTPRequest.put("account/following", jsonBody.toString(), currentToken, callback);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (followList == null) {
            return 0;
        }
        return followList.size();
    }

    public String getUsernameAt(int index) {
        return followList.get(index).getFollowerName();
    }

    public void showFollowDetail(String username) {
        listener.onDisplayFollowDetail(username);
    }
}


