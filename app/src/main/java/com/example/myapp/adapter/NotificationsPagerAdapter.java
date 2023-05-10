package com.example.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapp.fragment.notifications.LikesFragment;
import com.example.myapp.fragment.notifications.MessagesFragment;
import com.example.myapp.fragment.notifications.RepliesFragment;

public class NotificationsPagerAdapter extends FragmentStateAdapter {
    public static String[] tabTitles = {"消息", "收到的赞", "回复我的"};

    public NotificationsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        switch (position) {
            case 0:
                return new MessagesFragment();
            case 1:
                return new LikesFragment();
            default:
                return new RepliesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
