package com.example.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapp.fragment.home.PostListFragment;

public class PostsPagerAdapter extends FragmentStateAdapter {
    public static String[] tabTitles = {"全部", "热门", "关注"};
    public static String[] filter = {null, "hot", "following"};

    public PostsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        return PostListFragment.newInstance(null, filter[position], "time");
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
