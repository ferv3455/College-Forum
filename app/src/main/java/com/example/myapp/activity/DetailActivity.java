package com.example.myapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.adapter.GridViewAdapter;

public class DetailActivity extends AppCompatActivity {

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
        usernameView.setText(intent.getStringExtra("username"));
        datetimeView.setText(intent.getStringExtra("datetime"));
        tagView.setText(intent.getStringExtra("tag"));
        titleView.setText(intent.getStringExtra("title"));
        contentView.setText(intent.getStringExtra("content"));

        GridViewAdapter adapter = new GridViewAdapter(this, intent.getStringArrayExtra("images"));
        imagesView.setAdapter(adapter);
    }
}