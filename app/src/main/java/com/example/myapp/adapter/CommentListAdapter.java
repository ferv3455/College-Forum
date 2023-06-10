package com.example.myapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Comment;
import com.example.myapp.data.CommentList;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

class CommentViewHolder extends RecyclerView.ViewHolder {
    public final ImageView avatarView;
    public final TextView usernameView;
    public final TextView datetimeView;
    public final TextView contentView;
    public final TextView likesView;
    public final ImageView likesImage;
    public final LinearLayout likesLayout;
    public boolean isLiked;
    private final CommentListAdapter adapter;
    private final Context context;

    public CommentViewHolder(@NonNull View itemView, CommentListAdapter adapter, Context context) {
        super(itemView);
        this.adapter = adapter;
        this.context = context;
        avatarView = itemView.findViewById(R.id.avatar);
        usernameView = itemView.findViewById(R.id.username);
        datetimeView = itemView.findViewById(R.id.datetime);
        contentView = itemView.findViewById(R.id.postContent);
        likesView = itemView.findViewById(R.id.likeCommentView);
        likesImage = itemView.findViewById(R.id.likeCommentImage);
        likesLayout = itemView.findViewById(R.id.likeCommentLayout);

        likesLayout.setOnClickListener(v -> {
            if (!isLiked) {
                sendLikeRequest();
            }
        });
    }

    private void sendLikeRequest() {
        int position = this.getAdapterPosition();
        Comment currentComment = adapter.getCommentList().get(position);
        String url = String.format("forum/comment/like/%s", currentComment.getId());

        HTTPRequest.post(url, "{}", TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody ignored = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    isLiked = true;
                    currentComment.setIsLiked(true);
                    currentComment.setLikes(currentComment.getLikes() + 1);
                    ((AppCompatActivity) context).runOnUiThread(() -> {
                        likesView.setText(Integer.toString(currentComment.getLikes()));
                        updateLikesLayout();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

    public void updateLikesLayout() {
        if (isLiked) {
            likesImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_500)));
            likesView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_500)));
        } else {
            likesImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray)));
            likesView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray)));
        }
    }
}

public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private final CommentList commentList;
    private final LayoutInflater inflater;
    private final Context context;

    public CommentListAdapter(Context context, CommentList postList) {
        inflater = LayoutInflater.from(context);
        this.commentList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view
        View mItemView = inflater.inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(mItemView, this, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment post = commentList.get(position);
        holder.isLiked = post.getIsLiked();
        holder.updateLikesLayout();

        holder.usernameView.setText(post.getUsername());
        holder.datetimeView.setText(post.getCreatedAt());
        holder.contentView.setText(post.getContent());
        holder.likesView.setText(Integer.toString(post.getLikes()));

        byte[] decodedString = Base64.decode(post.getAvatar(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.avatarView.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public CommentList getCommentList() {
        return commentList;
    }
}
