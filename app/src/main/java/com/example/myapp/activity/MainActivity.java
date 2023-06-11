package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.myapp.R;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.navView);
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        String token = TokenManager.getSavedToken(this);
        String currentUsername = TokenManager.getSavedUsername(this);
        if (token != null && !token.isEmpty()) {
            // The user is logged in, get the following list
            HTTPRequest.get("account/following/" + currentUsername, token, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // handle the error
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            JSONArray followingArray = jsonObject.getJSONArray("following");
                            Set<String> followingUsernames = new HashSet<>();
                            for (int i = 0; i < followingArray.length(); i++) {
                                JSONObject followObj = followingArray.getJSONObject(i);
                                String followName = followObj.getJSONObject("user").getString("username");
                                followingUsernames.add(followName);
                            }

                            // Save the following list to SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("FollowListPrefs", MODE_PRIVATE);
                            sharedPreferences.edit().putStringSet("followingList", followingUsernames).apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void getFollowList(String currentUsername, String currentToken, Callback callback) {
        HTTPRequest.get("account/following/" + currentUsername, currentToken, callback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}