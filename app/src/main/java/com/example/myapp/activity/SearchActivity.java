package com.example.myapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import com.example.myapp.R;

public class SearchActivity extends AppCompatActivity {
    SearchView searchbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchbar = findViewById(R.id.result_searchbar);

        Intent intent = getIntent();
        searchbar.setQuery(intent.getStringExtra("query"), false);
    }
}