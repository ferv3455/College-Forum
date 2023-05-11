package com.example.myapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class User {
    private static final String PREF_FILE ="com.example.myapp.userSharedPrefs";
    private static final String TOKEN = "TOKEN";
    public static String token = null;

    public static String getSavedToken(Context context) {
        if (token == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
            token = preferences.getString(TOKEN, null);
        }
        return token;
    }

    public static boolean login(Context context, String username, String password) {
        String token = "";
        // TODO
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(TOKEN, token);
        preferencesEditor.apply();
        return true;
    }
}
