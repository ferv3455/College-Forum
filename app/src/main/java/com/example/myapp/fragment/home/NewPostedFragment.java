package com.example.myapp.fragment.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.activity.DetailActivity;
import com.example.myapp.activity.MainActivity;
import com.example.myapp.activity.NewPostActivity;
import com.example.myapp.adapter.PostListAdapter;
import com.example.myapp.R;
import com.example.myapp.data.Post;
import com.example.myapp.data.PostList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

public class NewPostedFragment extends Fragment implements PostListAdapter.PostDetailEventListener {

    private final static String STATE_POSTS = "STATE_POSTS";
    private PostList postList = null;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private PostListAdapter adapter;
    private View view;

    private final ActivityResultLauncher<Intent> newPostLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                Intent data = result.getData();
                Post post = data.getParcelableExtra(NewPostActivity.NEW_POST);
                postList.insert(0, post);
                adapter.notifyItemInserted(0);
                recyclerView.smoothScrollToPosition(0);
            }
        }
    );

    public NewPostedFragment() {
        // Required empty public constructor
    }

    public NewPostedFragment(int state, String username) {
        // State 0 or else: default
        // State 1: self
        // State 2: favorite
        switch(state){
            case 1:
                getSelfPostList(username);
                break;
            case 2:
                getFavoriteList(username);
                break;
            default: break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            postList = savedInstanceState.getParcelable(STATE_POSTS);
        }
        else {
            String[] titles = {"Hello there!", "Hi!", "Hello!", "Greetings!", "Thanks!"};
            String[] contents = {"first post", "second post", "third post", "forth post", "fifth post"};
            postList = new PostList();
            for (int i = 0; i < 5; i++) {
                postList.insert(new Post(titles[i], contents[i],
                        UUID.randomUUID().toString().substring(0, 6),
                        UUID.randomUUID().toString().substring(0, 4)));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_new_posted, container, false);
        adapter = new PostListAdapter(getContext(), postList, this);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> onCreatePost());
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(STATE_POSTS, postList);
    }

    @Override
    public void onDisplayPostDetail(int data) {
        Post post = postList.get(data);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("username", post.getUsername());
        intent.putExtra("datetime", post.getCreatedAt());
        intent.putExtra("tag", post.getTag());
        intent.putExtra("title", post.getIntro());
        intent.putExtra("content", post.getContent());
        intent.putExtra("images", post.getImages());
        startActivity(intent);
    }

    public void onCreatePost() {
        Intent intent = new Intent(getActivity(), NewPostActivity.class);
        newPostLauncher.launch(intent);
    }

    public void getSelfPostList(String username) {
        return;
    }
    public void getFavoriteList(String username) {
        return;
    }
}