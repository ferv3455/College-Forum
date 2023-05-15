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

public class FollowerListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FollowListAdapter followListAdapter;
    private FollowList followList; // Replace this with your actual FollowList class

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_space_follow, container, false);

        recyclerView = view.findViewById(R.id.follow_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        followList = getFollowList(); // Replace this with actual call to API

        followListAdapter = new FollowListAdapter(getContext(), followList, position -> {
            // handle Follow item click
        });
        recyclerView.setAdapter(followListAdapter);

        return view;
    }

    private FollowList getFollowList() {
        // TODO: Replace this with actual call to API
        FollowList follow_list = new FollowList();
        String testname = "123";
        Follow test = new Follow(testname);
        follow_list.insert(test);
        return follow_list;
    }
}
