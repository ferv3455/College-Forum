package com.example.myapp.fragment.notifications;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.R;
import com.example.myapp.adapter.MessageAdapter;
import com.example.myapp.data.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagesFragment extends Fragment {

    private final List<messages> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_notifications_messages, container, false);
        for (int i=0; i<5; i++) {
            messages.add(new messages(R.drawable.avatar,"JJY", "你说的对但原神是一款"));
        }
        recyclerView = view.findViewById(R.id.message_recycle);
        adapter = new MessageAdapter(context,messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }
}