package com.example.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapp.fragment.home.FollowedFragment;
import com.example.myapp.fragment.home.HotFragment;
import com.example.myapp.fragment.home.NewPostedFragment;
import com.example.myapp.fragment.home.NewRepliedFragment;

public class PostsPagerAdapter extends FragmentStateAdapter {
    public static String[] tabTitles = {"新发表", "新回复", "热门", "关注"};

    public PostsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        switch (position) {
            case 0:
                return new NewPostedFragment();
            case 1:
                return new NewRepliedFragment();
            case 2:
                return new HotFragment();
            default:
                return new FollowedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
