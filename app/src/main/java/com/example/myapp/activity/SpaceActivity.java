package com.example.myapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import org.json.JSONArray;
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

    public ImageView userAvatar;
    public TextView userName;
    public TextView userDescription;
    public Button followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space);

        userAvatar = findViewById(R.id.user_avatar);
        userName = findViewById(R.id.user_name);
        userDescription = findViewById(R.id.user_description);  // assuming you have this in your layout
        followButton = findViewById(R.id.follow_button);

        Intent intent = getIntent();
        if (intent.hasExtra("username")) {
            currentUsername = intent.getStringExtra("username");
            if (this.currentUsername.equals(TokenManager.getSavedUsername(this))) {
                followButton.setVisibility(View.INVISIBLE);
            }
            followButton.setVisibility(View.VISIBLE);
            currentToken = TokenManager.getSavedToken(this);
        } else {
            currentToken = TokenManager.getSavedToken(this);
            currentUsername = TokenManager.getSavedUsername(this);
            followButton.setVisibility(View.INVISIBLE);
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

        fragment = PostListFragment.newInstance(currentUsername, null, "time");

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fragment = PostListFragment.newInstance(currentUsername, null, "time");
                        break;
                    case 1:
                        fragment = PostListFragment.newInstance(currentUsername, "fav", "time");
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

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a JSON object with the usernames to follow
                JSONObject jsonObject = new JSONObject();
                try {
                    JSONArray usernameArray = new JSONArray();
                    // Add the current username to the list
                    usernameArray.put(currentUsername);
                    jsonObject.put("username", usernameArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // send the PUT request
                HTTPRequest.put("account/following", jsonObject.toString(), currentToken, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(() -> {
                            String followMessage = "";
                            if (response.isSuccessful()) {
                                followMessage = "关注成功";
                                followButton.setText("已关注");
                            } else {
                                followMessage = "关注失败, 请重试";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(SpaceActivity.this);
                            builder.setMessage(followMessage)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        });
                    }
                });
            }
        });

    }

}
