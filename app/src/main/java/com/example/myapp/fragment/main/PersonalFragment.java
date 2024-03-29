package com.example.myapp.fragment.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.example.myapp.R;
import com.example.myapp.activity.EditProfileActivity;
import com.example.myapp.activity.LoginActivity;
import com.example.myapp.activity.MainActivity;
import com.example.myapp.activity.SpaceActivity;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PersonalFragment extends Fragment {
    Button editProfileButton;
    Button personalSpaceButton;
    Button loginButton;
    Button logoutButton;
    TextView usernameView;
    TextView descriptionView;
    ImageView avatarView;

    private final ActivityResultLauncher<Intent> loginLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                usernameView.setText(TokenManager.getSavedUsername(getActivity()));
                logoutButton.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.GONE);
            }
        }
    );

    @Override
    public void onResume() {
        super.onResume();
        if (TokenManager.getSavedToken(getActivity()) == null) {
            usernameView.setText("未登录");
            logoutButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
        else {
            TokenManager.updateTokenValidity(getActivity(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(() -> {
                        usernameView.setText("未登录");
                        logoutButton.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    getActivity().runOnUiThread(() -> {
                        usernameView.setText(TokenManager.getSavedUsername(getActivity()));
                        logoutButton.setVisibility(View.VISIBLE);
                        loginButton.setVisibility(View.GONE);
                    });
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        usernameView = view.findViewById(R.id.user_name);
        descriptionView = view.findViewById(R.id.user_description);
        avatarView = view.findViewById(R.id.user_avatar);

        personalSpaceButton = view.findViewById(R.id.personalSpaceButton);
        personalSpaceButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SpaceActivity.class);
            startActivity(intent);
        });

        loginButton = view.findViewById(R.id.loginButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            loginLauncher.launch(intent);
        });
        logoutButton.setOnClickListener(v -> TokenManager.removeToken(getActivity(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                getActivity().runOnUiThread(() -> {
                    usernameView.setText("未登录");
                    logoutButton.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                });
            }
        }));

        logoutButton.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);

        if (TokenManager.getSavedToken(getActivity()) == null) {
            usernameView.setText("未登录");
            logoutButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
        else {
            TokenManager.updateTokenValidity(getActivity(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    getActivity().runOnUiThread(() -> {
                        usernameView.setText("未登录");
                        logoutButton.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    getActivity().runOnUiThread(() -> {
                        usernameView.setText(TokenManager.getSavedUsername(getActivity()));

                        HTTPRequest.get("account/profile/" + TokenManager.getSavedUsername(getActivity()), TokenManager.getSavedToken(getActivity()), new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                    return;
                                }

                                String responseBody = response.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    String avatarBase64 = jsonObject.getString("avatar");
                                    String description = jsonObject.getString("description");

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            descriptionView.setText(description);
                                            byte[] imageBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                                            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                            avatarView.setImageBitmap(decodedImage);
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    });
                }
            });
        }

        editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        return view;
    }
}