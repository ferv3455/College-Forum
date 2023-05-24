package com.example.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapp.R;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.fragment.home.PostListFragment;
import com.example.myapp.fragment.space.FollowListFragment;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.BreakIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SpaceActivity extends AppCompatActivity {

    // ... other fields ...

    private Fragment fragment;
    private String currentUsername;
    private String currentToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);

        ImageView userAvatar = findViewById(R.id.user_avatar);
        TextView userName = findViewById(R.id.user_name);
        TextView userDescription = findViewById(R.id.user_description);  // assuming you have this in your layout

        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            currentUsername = intent.getStringExtra("username");
        } else {
            currentToken = TokenManager.getSavedToken(this);
            currentUsername = TokenManager.getSavedUsername(this);
        }

        HTTPRequest.get("account/profile/" + currentUsername, currentToken, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // handle the error
                        e.printStackTrace();
                        userAvatar.setImageResource(R.drawable.avatar); // Replace with actual user avatar
                        userName.setText(currentUsername); // Replace with actual username
                        Intent intent = new Intent(SpaceActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String responseBody = response.body().string();
                                JSONObject jsonObject = new JSONObject(responseBody);
                                String avatarBase64 = jsonObject.getString("avatar");
                                String description = jsonObject.getString("description");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Decode base64 string to a Bitmap
                                        byte[] decodedString = Base64.decode(avatarBase64, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        userAvatar.setImageBitmap(decodedByte);
                                        userName.setText(currentUsername);
                                        userDescription.setText(description);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        fragment = PostListFragment.newInstance("time", 1);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment = PostListFragment.newInstance("time", 1);
                        break;
                    case 1:
                        fragment = PostListFragment.newInstance("time", 2);
                        break;
                    case 2:
                        fragment = new FollowListFragment(1, currentUsername, currentToken);
                        break;
                    case 3:
                        fragment = new FollowListFragment(2, currentUsername, currentToken);
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
