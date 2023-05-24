package com.example.myapp.connection;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public static void getMyPosts(String sortBy, Callback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("sortBy", sortBy);
        HTTPRequest.getWithParams("account/my_posts/", params, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("MyPostList", "retrieve my post list");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void getMyFavorites(String sortBy, Callback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("sortBy", sortBy);
        HTTPRequest.getWithParams("account/my_favorites/", params, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("MyFavorites", "retrieve my favorites list");
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

    public static void createPost(Context context, JSONObject data, Callback callback) {
        HTTPRequest.post("forum/posts/", data.toString(), TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("Post", "create a new post");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void uploadImage(Context context, String data, Callback callback) {
        try {
            // Initialize post data
            JSONObject obj = new JSONObject();
            obj.put("data", data);

            HTTPRequest.post("forum/image/", obj.toString(), TokenManager.getSavedToken(context), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (callback != null){
                        callback.onFailure(call, e);
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.d("Image", "update a new image");
                    if (callback != null) {
                        callback.onResponse(call, response);
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
