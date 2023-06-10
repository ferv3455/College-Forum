package com.example.myapp.fragment.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.R;
import com.example.myapp.adapter.LikesAdapter;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Like;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LikesFragment extends Fragment {

    private List<Like> likesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LikesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_notifications_likes, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String likesJson = sharedPref.getString("likesList", "");
        if (!likesJson.equals("")) {
            Type type = new TypeToken<ArrayList<Like>>() {}.getType();
            likesList = new Gson().fromJson(likesJson, type);
        } else {
            likesList = new ArrayList<>();
        }

        String token = TokenManager.getSavedToken(getContext());
        HTTPRequest.get("notification/likes/", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("LikesFragment","Network request failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        // 将返回的 JSON 数据解析为 FullMessage 类型的列表
                        JSONArray jsonArray = new JSONArray(responseData);
                        if(jsonArray.length()!=0) {
                            Log.e("还是能返回","为什么");
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            JSONObject userProfile = jsonObject.getJSONObject("user_profile");
                            JSONObject user = userProfile.getJSONObject("user");
                            String username = user.getString("username");
                            String avatar = userProfile.getString("avatar");
                            JSONObject post = jsonObject.getJSONObject("post");
                            String postId = post.getString("id");
                            likesList.add(new Like(avatar,username,postId));
                        }

                        String likesJson = new Gson().toJson(likesList);
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("likesList", likesJson);
                        editor.apply();


                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        String currentTime = dateFormat.format(new Date());
                        JSONObject json = new JSONObject();
                        json.put("time", currentTime);

                        HTTPRequest.post("notification/likes-received/", json.toString(), token, new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e("PostTime", "Failed to post time: " + e.getMessage(), e);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.i("PostTime", "Successfully posted time");
                                } else {
                                    throw new IOException("Unexpected code " + response);
                                }
                            }
                        });

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView = view.findViewById(R.id.like_recycle);
                                adapter = new LikesAdapter(context,likesList);
                                recyclerView.setAdapter(adapter);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                                layoutManager.setReverseLayout(true);
                                layoutManager.setStackFromEnd(true);
                                recyclerView.setLayoutManager(layoutManager);
                            }
                        });

                    } catch (JSONException e) {
                        // 处理 JSON 解析异常
                        e.printStackTrace();
                    }
                } else {
                    // 处理请求失败的情况
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
        return view;
    }
}