package com.example.myapp.fragment.space;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.adapter.FollowListAdapter;
import com.example.myapp.data.Follow;
import com.example.myapp.data.FollowList;

public class FollowListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FollowListAdapter followListAdapter;
    private FollowList followList; // Replace this with your actual FollowList class
    private int state = 0;
    private String username = "null";

    public FollowListFragment() {
        // Required empty public constructor
    }

    public FollowListFragment(int state, String username) {
        // State 0 or else: default
        // State 1: self
        // State 2: favorite
        this.state = state;
        this.username = username;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_space_follow, container, false);

        recyclerView = view.findViewById(R.id.follow_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        followList = getFollowList(this.state, this.username); // Replace this with actual call to API

        followListAdapter = new FollowListAdapter(getContext(), followList, position -> {
            // handle follow item click
        });
        recyclerView.setAdapter(followListAdapter);

        return view;
    }

    private FollowList getFollowList(int state, String username) {
        // TODO: Replace this with actual call to API
        Follow follow;
        if(state == 1) {
            follow = new Follow("DefaultFollowing");
        }
        else if(state == 2){
            follow = new Follow("DefaultFollower");
        }
        else {
            follow = new Follow("Default");
        }
        FollowList followList1 = new FollowList();
        followList1.insert(follow);
        followList1.insert(follow);
        followList1.insert(follow);
        followList1.insert(follow);
        return followList1;
    }
}

