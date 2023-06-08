package com.example.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapp.fragment.home.PostListFragment;

public class PostsPagerAdapter extends FragmentStateAdapter {
    public static String[] tabTitles = {"新发表", "新回复", "热门", "关注"};
    public static String[] sortBy = {"time", "time", "hot", "following"};

    public PostsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        return PostListFragment.newInstance(sortBy[position], 0);
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
