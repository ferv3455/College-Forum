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
import com.example.myapp.connection.ContentManager;
import com.example.myapp.data.Post;
import com.example.myapp.data.PostList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PostListFragment extends Fragment implements PostListAdapter.PostDetailEventListener {
    private static final String ARG_SORT_BY = "SORT_BY";
    private static final String ARG_STATE = "STATE";
    private final static String STATE_POSTS = "STATE_POSTS";
    private PostList postList = null;
    private String sortBy = "time";
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private PostListAdapter adapter = null;
    private View view;
    private int state = 0;

    private final ActivityResultLauncher<Intent> newPostLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                updatePostList();
            }
        }
    );

    public PostListFragment() {
        // Required empty public constructor
    }

    public static PostListFragment newInstance(String sortBy, int state) {
        // State for getting postList(default, self, favorite)
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SORT_BY, sortBy);
        args.putInt(ARG_STATE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortBy = getArguments().getString(ARG_SORT_BY);
            state = getArguments().getInt(ARG_STATE);
        }

        if (savedInstanceState != null) {
            postList = savedInstanceState.getParcelable(STATE_POSTS);
        }
        else {
            postList = new PostList();
            updatePostList();
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
        intent.putExtra("id", post.getId());
        startActivity(intent);
    }

    public void onCreatePost() {
        Intent intent = new Intent(getActivity(), NewPostActivity.class);
        newPostLauncher.launch(intent);
    }

    public void updatePostList() {
        if (state == 0) {
            ContentManager.getPostList(sortBy, new PostListCallback());
        }
        else if(state == 1) {
            ContentManager.getMyPosts(sortBy, new PostListCallback());
        }
        else if(state == 2) {
            ContentManager.getMyFavorites(sortBy, new PostListCallback());
        }
    }

    private class PostListCallback implements Callback {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            try (ResponseBody responseBody = response.body()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                assert responseBody != null;
                JSONArray result = new JSONArray(responseBody.string());
                postList.clear();
                postList.update(result);
                if (adapter != null) {
                    getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}