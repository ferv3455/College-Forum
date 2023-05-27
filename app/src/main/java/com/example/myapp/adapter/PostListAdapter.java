package com.example.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.myapp.data.Post;
import com.example.myapp.data.PostList;

class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final ImageView avatarView;
    public final TextView usernameView;
    public final TextView datetimeView;
    public final TextView tagView;
    public final TextView titleView;
    public final TextView contentView;
    public final GridView imagesView;
    public final TextView commentsView;
    public final TextView likesView;
    public final TextView starsView;
    private final PostListAdapter adapter;

    public PostViewHolder(@NonNull View itemView, PostListAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);
        avatarView = itemView.findViewById(R.id.avatar);
        usernameView = itemView.findViewById(R.id.username);
        datetimeView = itemView.findViewById(R.id.datetime);
        tagView = itemView.findViewById(R.id.tag);
        titleView = itemView.findViewById(R.id.postTitle);
        contentView = itemView.findViewById(R.id.postContent);
        imagesView = itemView.findViewById(R.id.imagesGridView);
        imagesView.setVerticalScrollBarEnabled(false);
        commentsView = itemView.findViewById(R.id.commentView);
        likesView = itemView.findViewById(R.id.likeView);
        starsView = itemView.findViewById(R.id.starView);
    }

    @Override
    public void onClick(View view) {
        int position = this.getAdapterPosition();
        adapter.showPostDetail(position);
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
        return new PostViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.usernameView.setText(post.getUsername());
        holder.datetimeView.setText(post.getCreatedAt());
        holder.tagView.setText(post.getTag());
        holder.titleView.setText(post.getIntro());
        holder.contentView.setText(post.getContent());
        holder.commentsView.setText(Integer.toString(post.getComments()));
        holder.likesView.setText(Integer.toString(post.getLikes()));
        holder.starsView.setText(Integer.toString(post.getStars()));

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
