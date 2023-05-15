package com.example.myapp.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapp.R;
import com.example.myapp.fragment.home.NewPostedFragment;
import com.example.myapp.fragment.space.FollowerListFragment;
import com.example.myapp.fragment.space.FollowingListFragment;
import com.google.android.material.tabs.TabLayout;

public class SpaceActivity extends AppCompatActivity {

    // ... other fields ...

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);

        ImageView userAvatar = findViewById(R.id.user_avatar);
        TextView userName = findViewById(R.id.user_name);

        // Set user avatar and name
        userAvatar.setImageResource(R.drawable.avatar);  // Replace with actual user avatar
        userName.setText("Username");  // Replace with actual username

        fragment = new NewPostedFragment();

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new NewPostedFragment();
                        break;
                    case 1:
                        fragment = new NewPostedFragment();
                        break;
                    case 2:
                        fragment = new FollowingListFragment();
                        break;
                    case 3:
                        fragment = new FollowerListFragment();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected position: " + tab.getPosition());
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

}
