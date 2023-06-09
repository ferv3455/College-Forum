package com.example.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Post;
import com.example.myapp.data.PostList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final ImageView avatarView;
    public final TextView usernameView;
    public final TextView datetimeView;
    public final TextView tag1View;
    public final TextView tag2View;
    public final TextView titleView;
    public final TextView contentView;
    public final GridView imagesView;
    public final TextView commentsView;
    public final TextView likesView;
    public final TextView starsView;
    public boolean isStarred;
    private final PostListAdapter adapter;
    private final Context context;

    public PostViewHolder(@NonNull View itemView, PostListAdapter adapter, Context context) {
        super(itemView);
        this.adapter = adapter;
        this.context = context;
        itemView.setOnClickListener(this);
        avatarView = itemView.findViewById(R.id.avatar);
        usernameView = itemView.findViewById(R.id.username);
        datetimeView = itemView.findViewById(R.id.datetime);
        tag1View = itemView.findViewById(R.id.tag1);
        tag2View = itemView.findViewById(R.id.tag2);
        titleView = itemView.findViewById(R.id.postTitle);
        contentView = itemView.findViewById(R.id.postContent);
        imagesView = itemView.findViewById(R.id.imagesGridView);
        imagesView.setVerticalScrollBarEnabled(false);
        commentsView = itemView.findViewById(R.id.commentView);
        likesView = itemView.findViewById(R.id.likeView);
        starsView = itemView.findViewById(R.id.starView);

        starsView.setOnClickListener(v -> {
            if (isStarred) {
                sendUnstarRequest();
            } else {
                sendStarRequest();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int position = this.getAdapterPosition();
        adapter.showPostDetail(position);
    }

    private void sendStarRequest() {
        int position = this.getAdapterPosition();
        Post currentPost = adapter.getPostList().get(position);
        String postId = currentPost.getId();

        JSONObject jsonParams = new JSONObject();
        JSONArray idArray = new JSONArray();
        idArray.put(postId);
        try {
            jsonParams.put("id", idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HTTPRequest.put("account/favorites", jsonParams.toString(), TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 处理回应...
                // 如果成功，那么应该设置 isStarred 为 true，然后更新 starsView 的文字颜色（或其他的标记）
                isStarred = true;
                currentPost.setIsStarred(true);
                starsView.setTextColor(Color.BLUE);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

    private void sendUnstarRequest() {
        int position = this.getAdapterPosition();
        Post currentPost = adapter.getPostList().get(position);
        String postId = currentPost.getId();

        JSONObject jsonParams = new JSONObject();
        JSONArray idArray = new JSONArray();
        idArray.put(postId);
        try {
            jsonParams.put("id", idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HTTPRequest.delete("account/favorites", jsonParams.toString(), TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 处理回应...
                // 如果成功，那么应该设置 isStarred 为 false，然后更新 starsView 的文字颜色（或其他的标记）
                isStarred = false;
                currentPost.setIsStarred(false);
                starsView.setTextColor(Color.BLACK);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

}

public class PostListAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private final PostList postList;
    private final LayoutInflater inflater;
    private final Context context;
    private final PostDetailEventListener listener;

    public interface PostDetailEventListener {
        void onDisplayPostDetail(int data);
    }

    public PostListAdapter(Context context, PostList postList, PostDetailEventListener listener) {
        inflater = LayoutInflater.from(context);
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view
        View mItemView = inflater.inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(mItemView, this, context);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.isStarred = post.getIsStarred();
        if (holder.isStarred) {
            holder.starsView.setTextColor(Color.BLUE); // 这只是个例子，你可以根据你的 UI 设计来设置
        } else {
            holder.starsView.setTextColor(Color.BLACK); // 这只是个例子，你可以根据你的 UI 设计来设置
        }

        holder.usernameView.setText(post.getUsername());
        holder.datetimeView.setText(post.getCreatedAt());
        holder.titleView.setText(post.getIntro());
        holder.contentView.setText(post.getContent());
        holder.commentsView.setText(Integer.toString(post.getComments()));
        holder.likesView.setText(Integer.toString(post.getLikes()));
        holder.starsView.setText(Integer.toString(post.getStars()));

        String[] tags = post.getTags();
        if (tags.length == 0) {
            holder.tag1View.setVisibility(View.GONE);
            holder.tag2View.setVisibility(View.GONE);
        }
        else if (tags.length == 1) {
            holder.tag1View.setVisibility(View.GONE);
            holder.tag2View.setText(tags[0]);
        }
        else {
            holder.tag1View.setText(tags[0]);
            holder.tag2View.setText(tags[1]);
        }

        byte[] decodedString = Base64.decode(post.getAvatar(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.avatarView.setImageBitmap(decodedByte);

        GridViewAdapter adapter = new GridViewAdapter(context, post.getImages());
        holder.imagesView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public PostList getPostList() {
        return postList;
    }

    public void showPostDetail(int index) {
        listener.onDisplayPostDetail(index);
    }
}
