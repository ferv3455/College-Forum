package com.example.myapp.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.myapp.R;
import com.example.myapp.adapter.LoginPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LoginActivity extends FragmentActivity {
    LoginPagerAdapter adapter;
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        adapter = new LoginPagerAdapter(this);
        viewPager = findViewById(R.id.login_pager);
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.login_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(LoginPagerAdapter.tabTitles[position])
        ).attach();
    }
}