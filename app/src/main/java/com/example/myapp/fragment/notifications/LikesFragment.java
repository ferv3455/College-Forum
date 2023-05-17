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
import com.example.myapp.adapter.LikesAdapter;
import com.example.myapp.data.Like;


import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    private final List<Like> likesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LikesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_notifications_likes, container, false);
        for (int i=0; i<5; i++) {
            likesList.add(new Like(R.drawable.avatar,"byt"));
        }
        recyclerView = view.findViewById(R.id.like_recycle);
        adapter = new LikesAdapter(context,likesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }
}