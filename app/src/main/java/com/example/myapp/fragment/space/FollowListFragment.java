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
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.data.Follow;
import com.example.myapp.data.FollowList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FollowListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FollowListAdapter followListAdapter;
    private FollowList followList; // Replace this with your actual FollowList class
    private int state = 0;
    private String currentUsername = "null";
    private String currentToken = "null";

    public FollowListFragment() {
        // Required empty public constructor
    }

    public FollowListFragment(int state, String currentUsername, String currentToken) {
        // State 0 or else: default
        // State 1: self
        // State 2: favorite
        this.state = state;
        this.currentUsername = currentUsername;
        this.currentToken = currentToken;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_space_follow, container, false);

        recyclerView = view.findViewById(R.id.follow_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getFollowList(this.state, this.currentUsername, this.currentToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // handle the error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray followingArray = jsonObject.getJSONArray("following");
                        FollowList followList = new FollowList();
                        for (int i = 0; i < followingArray.length(); i++) {
                            JSONObject followObj = followingArray.getJSONObject(i);
                            int tempId = followObj.getJSONObject("user").getInt("id");
                            String followerName = followObj.getJSONObject("user").getString("username");
                            String avatar = followObj.getString("avatar");
                            String description = followObj.getString("description");
                            Follow follow = new Follow(tempId, followerName, avatar, description);
                            followList.insert(follow);
                        }
                        // Now followList contains the list of people the user is following
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // Replace this with actual call to API

        followListAdapter = new FollowListAdapter(getContext(), followList, position -> {
            // handle follow item click
        });
        recyclerView.setAdapter(followListAdapter);

        return view;
    }

    private void getFollowList(int state, String currentUsername, String currentToken, Callback callback) {
        if(state == 1) {
            // Get Following List
            HTTPRequest.get("account/following/" + currentUsername, currentToken, callback);
        }
        else if(state == 2){
            // Get Follower List
            HTTPRequest.get("account/followed/" + currentUsername, currentToken, callback);
        }
        else {
            // Handle other states
        }
    }
}

