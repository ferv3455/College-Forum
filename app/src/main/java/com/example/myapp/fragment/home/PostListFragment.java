package com.example.myapp.fragment.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.myapp.activity.DetailActivity;
import com.example.myapp.activity.MainActivity;
import com.example.myapp.activity.MediaActivity;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PostListFragment extends Fragment
        implements PostListAdapter.PostDetailEventListener, PopupMenu.OnMenuItemClickListener {
    private static final List<String> sortParams = Arrays.asList("time", "comment-time", "likes", "comments");
    private static final String ARG_USER = "USER";
    private static final String ARG_FILTER = "FILTER";
    private static final String ARG_SORT_BY = "SORT_BY";
    private final static String STATE_POSTS = "STATE_POSTS";
    private PostList postList = null;
    private String user = null; // null: home page
    private String filter = null; // null: all
    private String sortBy = "time";
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private FloatingActionButton sortButton;
    private PostListAdapter adapter = null;
    private PopupMenu menu = null;
    private View view;

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

    public static PostListFragment newInstance(String user, String filter, String sortBy) {
        // State for getting postList(default, self, favorite)
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        args.putString(ARG_FILTER, filter);
        args.putString(ARG_SORT_BY, sortBy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_USER);
            filter = getArguments().getString(ARG_FILTER);
            sortBy = getArguments().getString(ARG_SORT_BY);
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
        view = inflater.inflate(R.layout.fragment_home_postlist, container, false);

        refreshLayout = view.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this::updatePostList);

        adapter = new PostListAdapter(getContext(), postList, this);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> onCreatePost());

        if (user != null) {
            addButton.setVisibility(View.GONE);
        }

        sortButton = view.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(view -> {
            if (menu != null) {
                menu.dismiss();
            }
            menu = new PopupMenu(getContext(), view);
            menu.getMenuInflater().inflate(R.menu.sort_menu, menu.getMenu());
            setMenuItemChecked(menu);
            menu.setOnMenuItemClickListener(this);
            menu.show();
        });

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

    public void setMenuItemChecked(PopupMenu menu) {
        int index = sortParams.indexOf(sortBy);
        MenuItem item = menu.getMenu().getItem(index);
        item.setChecked(true);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_time:
                sortBy = "time";
                break;
            case R.id.sort_comment_time:
                sortBy = "comment-time";
                break;
            case R.id.sort_likes:
                sortBy = "likes";
                break;
            case R.id.sort_comments:
                sortBy = "comments";
                break;
            default:
                menu.dismiss();
                menu = null;
                return false;
        }
        menu.dismiss();
        menu = null;
        updatePostList();
        return true;
    }

    public void updatePostList() {
        if (user == null) {
            // Home page
            ContentManager.getPostList(filter, sortBy, getActivity(), new PostListCallback());
        }
        else {
            // Personal space
            if (filter == null) {
                ContentManager.getUserPosts(user, sortBy, getActivity(), new PostListCallback());
            }
            else {
                ContentManager.getUserFavorites(user, sortBy, getActivity(), new PostListCallback());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (menu != null) {
            menu.dismiss();
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
                    postList.clear();
                    getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                    throw new IOException("Unexpected code " + response);
                }

                assert responseBody != null;
                JSONArray result = new JSONArray(responseBody.string());
                postList.clear();
                postList.update(result);
                if (adapter != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    });
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}