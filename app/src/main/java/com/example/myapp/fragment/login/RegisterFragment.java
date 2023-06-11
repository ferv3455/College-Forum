package com.example.myapp.fragment.login;

import static com.example.myapp.connection.TokenManager.token;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapp.R;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterFragment extends Fragment {
    EditText usernameEdit;
    EditText passwordEdit;
    Button registerButton;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        usernameEdit = view.findViewById(R.id.usernameRegisterEdit);
        passwordEdit = view.findViewById(R.id.passwordRegisterEdit);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> TokenManager.registerToken(getActivity(),
                usernameEdit.getText().toString(), passwordEdit.getText().toString(),
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MY_APP", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", usernameEdit.getText().toString());
                        editor.putString("password", passwordEdit.getText().toString());
                        editor.apply();

                        HTTPRequest.get("account/following/" + usernameEdit.getText().toString(), token, new Callback() {
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

                                        // Save the following list
                                        SharedPreferences prefs = getActivity().getSharedPreferences("FollowListPrefs", Context.MODE_PRIVATE);
                                        prefs.edit().putStringSet("followingList", followingUsernames).apply();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        Intent replyIntent = new Intent();
                        getActivity().setResult(Activity.RESULT_OK, replyIntent);
                        getActivity().finish();
                    }
                }));

        return view;
    }
}