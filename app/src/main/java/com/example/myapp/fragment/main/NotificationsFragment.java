package com.example.myapp.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.R;
import com.example.myapp.adapter.NotificationsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NotificationsFragment extends Fragment {
    NotificationsPagerAdapter adapter;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        adapter = new NotificationsPagerAdapter(this);
        viewPager = view.findViewById(R.id.notifications_pager);
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.notifications_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(NotificationsPagerAdapter.tabTitles[position])
        ).attach();
        return view;
    }
}