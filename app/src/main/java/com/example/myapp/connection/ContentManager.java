package com.example.myapp.connection;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.*;

public class ContentManager {
    public static void getPostList(String filter, String sortBy, Context context, Callback callback) {
        HashMap<String, String> params = new HashMap<>();
        if (filter != null) {
            params.put("filter", filter);
        }
        if (sortBy != null) {
            params.put("sortBy", sortBy);
        }
        HTTPRequest.getWithParams("forum/posts/", params, TokenManager.getSavedToken(context), new Callback() {
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

    public static void getUserPosts(String username, String sortBy, Context context, Callback callback) {
        if (username == null) {
            username = TokenManager.getSavedUsername(context);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("user", username);
        if (sortBy != null) {
            params.put("sortBy", sortBy);
        }
        HTTPRequest.getWithParams("forum/posts/", params, TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("UserPostList", "retrieve user's post list");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void getUserFavorites(String username, String sortBy, Context context, Callback callback) {
        if (username != null) {
            username = "/" + username;
        }
        else {
            username = "";
        }

        HashMap<String, String> params = new HashMap<>();
        if (sortBy != null) {
            params.put("sortBy", sortBy);
        }

        HTTPRequest.getWithParams("account/favorites" + username, params, TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (callback != null){
                    callback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("UserFavorites", "retrieve user's favorites list");
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }
        });
    }

    public static void searchPostList(String queryString, String sortBy, Context context, Callback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("query", queryString);
        if (sortBy != null) {
            params.put("sortBy", sortBy);
        }
        HTTPRequest.getWithParams("forum/posts/", params, TokenManager.getSavedToken(context), new Callback() {
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

    public static void getPostDetail(String id, Context context, Callback callback) {
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

    public static void getPostComments(String id, Context context, Callback callback) {
        HTTPRequest.get(String.format("forum/comment/%s", id), TokenManager.getSavedToken(context),
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (callback != null){
                            callback.onFailure(call, e);
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d("CommentList", "retrieve the comment list");
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

    public static void uploadMedia(Context context, File file, Callback callback) {
        HTTPRequest.post("forum/file/", file, TokenManager.getSavedToken(context), new Callback() {
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
    }
}
