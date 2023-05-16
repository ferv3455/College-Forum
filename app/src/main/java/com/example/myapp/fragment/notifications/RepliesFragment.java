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
import com.example.myapp.adapter.RepliesAdapter;
import com.example.myapp.data.replies;

import java.util.ArrayList;
import java.util.List;

public class RepliesFragment extends Fragment {

    private final List<replies> repliesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RepliesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_notifications_replies, container, false);
        for (int i=0; i<5; i++) {
            repliesList.add(new replies(R.drawable.avatar,"byt", "你说的对但原神是一款"));
        }
        recyclerView = view.findViewById(R.id.reply_recycle);
        adapter = new RepliesAdapter(context,repliesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }
}