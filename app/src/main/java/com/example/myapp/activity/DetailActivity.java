package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.adapter.GridViewAdapter;
import com.example.myapp.connection.ContentManager;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DetailActivity extends AppCompatActivity {
    private Post post = null;
    private ImageView avatarView;
    private TextView usernameView;
    private TextView datetimeView;
    private TextView tag1View;
    private TextView tag2View;
    private TextView titleView;
    private TextView contentView;
    private GridView imagesView;
    private LinearLayout locationLayout;
    private TextView locationView;
    private TextView likesView;
    private TextView starsView;
    private ImageView likesImage;
    private ImageView starsImage;
    private LinearLayout likesLayout;
    private LinearLayout starsLayout;
    private LinearLayout shareLayout;
    private boolean isLiked;
    private boolean isStarred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        avatarView = findViewById(R.id.avatar);
        usernameView = findViewById(R.id.username);
        datetimeView = findViewById(R.id.datetime);
        tag1View = findViewById(R.id.tag1);
        tag2View = findViewById(R.id.tag2);
        titleView = findViewById(R.id.postTitle);
        contentView = findViewById(R.id.postContent);
        imagesView = findViewById(R.id.imagesGridView);
        locationLayout = findViewById(R.id.locationLayout);
        locationView = findViewById(R.id.locationTextView);

        likesView = findViewById(R.id.likeView);
        starsView = findViewById(R.id.starView);
        likesImage = findViewById(R.id.likeImage);
        starsImage = findViewById(R.id.starImage);
        likesLayout = findViewById(R.id.likeLayout);
        starsLayout = findViewById(R.id.starLayout);
        shareLayout = findViewById(R.id.shareLayout);

        likesLayout.setOnClickListener(v -> {
            if (isLiked) {
                sendUnlikeRequest();
            } else {
                sendLikeRequest();
            }
        });
        starsLayout.setOnClickListener(v -> {
            if (isStarred) {
                sendUnstarRequest();
            } else {
                sendStarRequest();
            }
        });
        shareLayout.setOnClickListener(v -> {
            Log.d("Share", "here");
        });

        imagesView.setVerticalScrollBarEnabled(false);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        ContentManager.getPostDetail(id, this, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    assert responseBody != null;
                    JSONObject result = new JSONObject(responseBody.string());
                    post = new Post(result, true);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(() -> {
                    usernameView.setText(post.getUsername());
                    datetimeView.setText(post.getCreatedAt());
                    titleView.setText(post.getIntro());
                    contentView.setText(post.getContent());
                    likesView.setText(Integer.toString(post.getLikes()));
                    starsView.setText(Integer.toString(post.getStars()));

                    if (post.getLocation() != null) {
                        locationView.setText(post.getLocation());
                    }
                    else {
                        locationLayout.setVisibility(View.GONE);
                    }

                    isLiked = post.getIsLiked();
                    isStarred = post.getIsStarred();
                    updateLikesLayout();
                    updateStarsLayout();

                    String[] tags = post.getTags();
                    if (tags.length == 0) {
                        tag1View.setVisibility(View.GONE);
                        tag2View.setVisibility(View.GONE);
                    }
                    else if (tags.length == 1) {
                        tag1View.setVisibility(View.GONE);
                        tag2View.setVisibility(View.VISIBLE);
                        tag2View.setText(tags[0]);
                    }
                    else {
                        tag1View.setVisibility(View.VISIBLE);
                        tag2View.setVisibility(View.VISIBLE);
                        tag1View.setText(tags[0]);
                        tag2View.setText(tags[1]);
                    }

                    byte[] decodedString = Base64.decode(post.getAvatar(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    avatarView.setImageBitmap(decodedByte);

                    GridViewAdapter adapter = new GridViewAdapter(imagesView.getContext(), post.getImages());
                    imagesView.setAdapter(adapter);
                });
            }
        });
    }

    private void sendStarRequest() {
        String postId = post.getId();
        JSONObject jsonParams = new JSONObject();
        JSONArray idArray = new JSONArray();
        idArray.put(postId);
        try {
            jsonParams.put("id", idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HTTPRequest.put("account/favorites", jsonParams.toString(), TokenManager.getSavedToken(this), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody ignored = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    isStarred = true;
                    post.setIsStarred(true);
                    post.setStars(post.getStars() + 1);
                    runOnUiThread(() -> {
                        starsView.setText(Integer.toString(post.getStars()));
                        updateStarsLayout();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

    private void sendUnstarRequest() {
        String postId = post.getId();
        JSONObject jsonParams = new JSONObject();
        JSONArray idArray = new JSONArray();
        idArray.put(postId);
        try {
            jsonParams.put("id", idArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HTTPRequest.delete("account/favorites", jsonParams.toString(), TokenManager.getSavedToken(this), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody ignored = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    isStarred = false;
                    post.setIsStarred(false);
                    post.setStars(post.getStars() - 1);
                    runOnUiThread(() -> {
                        starsView.setText(Integer.toString(post.getStars()));
                        updateStarsLayout();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

    private void sendLikeRequest() {
        String url = String.format("forum/like/%s", post.getId());

        HTTPRequest.post(url, "{}", TokenManager.getSavedToken(this), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody ignored = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    isLiked = true;
                    post.setIsLiked(true);
                    post.setLikes(post.getLikes() + 1);
                    runOnUiThread(() -> {
                        likesView.setText(Integer.toString(post.getLikes()));
                        updateLikesLayout();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }

    private void sendUnlikeRequest() {
        String url = String.format("forum/like/%s", post.getId());

        HTTPRequest.delete(url, "{}", TokenManager.getSavedToken(this), new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody ignored = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    isLiked = false;
                    post.setIsLiked(false);
                    post.setLikes(post.getLikes() - 1);
                    runOnUiThread(() -> {
                        likesView.setText(Integer.toString(post.getLikes()));
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
            likesImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            likesView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
        } else {
            likesImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
            likesView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        }
    }

    public void updateStarsLayout() {
        if (isStarred) {
            starsImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            starsView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
        } else {
            starsImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
            starsView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        }
    }
}