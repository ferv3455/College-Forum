package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
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

    private TextView usernameView;
    private TextView datetimeView;
    private TextView tagView;
    private TextView titleView;
    private TextView contentView;
    private GridView imagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        usernameView = findViewById(R.id.username);
        datetimeView = findViewById(R.id.datetime);
        tagView = findViewById(R.id.tag);
        titleView = findViewById(R.id.postTitle);
        contentView = findViewById(R.id.postContent);
        imagesView = findViewById(R.id.imagesGridView);
        imagesView.setVerticalScrollBarEnabled(false);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        ContentManager.getPostDetail(this, id, new Callback() {
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
                    tagView.setText(post.getTag());
                    titleView.setText(post.getIntro());
                    contentView.setText(post.getContent());
                    GridViewAdapter adapter = new GridViewAdapter(imagesView.getContext(), post.getImages());
                    imagesView.setAdapter(adapter);
                });
            }
        });
    }
}