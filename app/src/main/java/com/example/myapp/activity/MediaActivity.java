package com.example.myapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class MediaActivity extends AppCompatActivity {
    WebView mediaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String url = intent.getStringExtra("url");
        String html;

        if (type.equals("png") || type.equals("jpg") || type.equals("jpeg")) {
            html = String.format("<img src=\"%s\" />", url);
        }
        else if (type.equals("mp4")) {
            html = String.format("<video width=\"320\" height=\"240\" controls>" +
                    "<source src=\"%s\" type=\"video/mp4\"></video>", url);
        }
        else {
            throw new RuntimeException("Unknown media format");
        }

        Log.d("html", html);

        mediaView = findViewById(R.id.mediaView);
        mediaView.loadData(html, "text/html", "UTF-8");
    }
}