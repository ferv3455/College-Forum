package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.adapter.GridViewAdapter;
import com.example.myapp.connection.ContentManager;
import com.example.myapp.data.Post;

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
    private TextView tagView;
    private TextView titleView;
    private TextView contentView;
    private GridView imagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    setContentView(R.layout.activity_detail);

                    avatarView = findViewById(R.id.avatar);
                    usernameView = findViewById(R.id.username);
                    datetimeView = findViewById(R.id.datetime);
                    tagView = findViewById(R.id.tag);
                    titleView = findViewById(R.id.postTitle);
                    contentView = findViewById(R.id.postContent);
                    imagesView = findViewById(R.id.imagesGridView);
                    imagesView.setVerticalScrollBarEnabled(false);

                    usernameView.setText(post.getUsername());
                    datetimeView.setText(post.getCreatedAt());
                    tagView.setText(post.getTag());
                    titleView.setText(post.getIntro());
                    contentView.setText(post.getContent());

                    byte[] decodedString = Base64.decode(post.getAvatar(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    avatarView.setImageBitmap(decodedByte);

                    GridViewAdapter adapter = new GridViewAdapter(imagesView.getContext(), post.getImages());
                    imagesView.setAdapter(adapter);
                });
            }
        });
    }
}