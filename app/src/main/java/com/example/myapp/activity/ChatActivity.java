package com.example.myapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.adapter.ChatHistoryAdapter;
import com.example.myapp.data.Message;
import com.example.myapp.data.MessageList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {
    TextView titleView;
    RecyclerView recyclerView;
    ChatHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        titleView = findViewById(R.id.chatActivityTitle);
        recyclerView = findViewById(R.id.chatRecyclerView);

        MessageList msgList = new MessageList();
        msgList.list().add(new Message("this is good", true));
        msgList.list().add(new Message("this is bad", false));

        adapter = new ChatHistoryAdapter(this, msgList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        titleView.setText(intent.getStringExtra("username"));
    }
}