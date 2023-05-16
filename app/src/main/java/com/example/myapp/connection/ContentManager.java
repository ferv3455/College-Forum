package com.example.myapp.connection;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.*;

public class ContentManager {
    public static void getPostList(String sortBy, Callback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("sortBy", sortBy);
        HTTPRequest.getWithParams("forum/posts/", params, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("PostList", "retrieve post list");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void getPostDetail(Context context, String id, Callback callback) {
        HTTPRequest.get(String.format("forum/posts/%s", id), TokenManager.getSavedToken(context),
                new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("PostList", "retrieve post detail");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }
}
