package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.myapp.R;
import com.example.myapp.adapter.PostListAdapter;
import com.example.myapp.connection.ContentManager;
import com.example.myapp.data.Post;
import com.example.myapp.data.PostList;
import com.example.myapp.fragment.home.PostListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchActivity extends AppCompatActivity
        implements PostListAdapter.PostDetailEventListener, SearchView.OnQueryTextListener {
    private SearchView searchbar;
    private String query;
    private PostList postList = new PostList();
    private String sortBy = "time";
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private PostListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        query = intent.getStringExtra("query");
        updatePostList();

        refreshLayout = findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this::updatePostList);

        adapter = new PostListAdapter(this, postList, this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchbar = findViewById(R.id.result_searchbar);
        searchbar.setSubmitButtonEnabled(true);
        searchbar.setOnQueryTextListener(this);
        searchbar.setQuery(query, false);
    }

    @Override
    public void onDisplayPostDetail(int data) {
        Post post = postList.get(data);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", post.getId());
        startActivity(intent);
    }

    public void updatePostList() {
        ContentManager.searchPostList(query, sortBy, this, new SearchListCallback());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.query = query;
        updatePostList();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.requestFocus();
    }

    private class SearchListCallback implements Callback {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            try (ResponseBody responseBody = response.body()) {
                if (!response.isSuccessful()) {
                    postList.clear();
                    runOnUiThread(() -> refreshLayout.setRefreshing(false));
                    throw new IOException("Unexpected code " + response);
                }

                assert responseBody != null;
                JSONArray result = new JSONArray(responseBody.string());
                postList.clear();
                postList.update(result);
                if (adapter != null) {
                    runOnUiThread(() -> {
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