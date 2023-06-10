package com.example.myapp.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.*;

import org.json.*;

import java.io.IOException;

public class TokenManager {
    private static final String PREF_FILE ="com.example.myapp.userSharedPrefs";
    private static final String TOKEN = "TOKEN";
    private static final String USERNAME = "USERNAME";
    public static String token = null;
    public static String user = null;

    // Storage - Memory transfer
    public static String getSavedToken(Context context) {
        if (token == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
            token = preferences.getString(TOKEN, null);
            user = preferences.getString(USERNAME, null);
        }
        return token;
    }

    public static void changeUsername(String username, Context context) {
        user = username;
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(USERNAME, user);
        return;
    }

    public static String getSavedUsername(Context context) {
        if (token == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
            token = preferences.getString(TOKEN, null);
            user = preferences.getString(USERNAME, null);
        }
        return user;
    }

    public static void saveToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(TOKEN, token);
        preferencesEditor.putString(USERNAME, user);
        preferencesEditor.apply();
    }

    public static void deleteToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

    // Retrieve token from server
    public static void renewToken(Context context, String username, String password, Callback callback) {
        try {
            // Initialize post data
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);

            HTTPRequest.post("auth/login/", obj.toString(), null, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (callback != null){
                        callback.onFailure(call, e);
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        assert responseBody != null;
                        JSONObject result = new JSONObject(responseBody.string());
                        user = username;
                        token = (String) result.get("token");
                        saveToken(context);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("Token", "token renewed");
                    if (callback != null) {
                        callback.onResponse(call, response);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeToken(Context context, Callback callback) {
        HTTPRequest.get("auth/logout/", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    token = null;
                    user = null;
                    deleteToken(context);
                }
                Log.d("Token", "token removed");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void registerToken(Context context, String username, String password, Callback callback) {
        try {
            // Initialize post data
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);

            HTTPRequest.post("auth/register/", obj.toString(), null, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (callback != null){
                        callback.onFailure(call, e);
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        }

                        assert responseBody != null;
                        JSONObject result = new JSONObject(responseBody.string());
                        user = username;
                        token = (String) result.get("token");
                        saveToken(context);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("Token", "account created");
                    if (callback != null) {
                        callback.onResponse(call, response);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateTokenValidity(Context context, Callback callback) {
        HTTPRequest.get("auth/check-valid/", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                token = null;
                user = null;
                deleteToken(context);
                Log.d("Token", "token invalid");
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Token", "token valid");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}
