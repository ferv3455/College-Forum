package com.example.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapp.fragment.login.LoginFragment;
import com.example.myapp.fragment.login.RegisterFragment;

public class LoginPagerAdapter extends FragmentStateAdapter {
        public static String[] tabTitles = {"登录", "注册"};

        public LoginPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            if (position == 0) {
                return new LoginFragment();
            }
            else {
                return new RegisterFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

