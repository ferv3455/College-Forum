package com.example.myapp.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.myapp.activity.SearchActivity;
import com.example.myapp.adapter.PostsPagerAdapter;
import com.example.myapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener {
    PostsPagerAdapter adapter;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    SearchView searchbar;
    View view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        adapter = new PostsPagerAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(PostsPagerAdapter.tabTitles[position])
        ).attach();
        searchbar = view.findViewById(R.id.searchbar);
        searchbar.setOnQueryTextListener(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
}