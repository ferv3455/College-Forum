package com.example.myapp.fragment.notifications;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapp.R;
import com.example.myapp.activity.ChatActivity;

public class MessagesFragment extends Fragment {
    Button chatButtonForDebugging;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications_messages, container, false);
        chatButtonForDebugging = view.findViewById(R.id.chatButtonForDebugging);
        chatButtonForDebugging.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("username", "测试用户");
            intent.putExtra("userid", 1234);
            startActivity(intent);
        });
        return view;
    }
}