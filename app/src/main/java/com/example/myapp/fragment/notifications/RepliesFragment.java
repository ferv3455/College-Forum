package com.example.myapp.fragment.notifications;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.myapp.adapter.RepliesAdapter;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Like;
import com.example.myapp.data.Reply;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RepliesFragment extends Fragment {

    private List<Reply> repliesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RepliesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_notifications_replies, container, false);

        // 获取sharedpreference
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String repliesJson = sharedPref.getString("repliesList", "");
        if (!repliesJson.equals("")) {
            Type type = new TypeToken<ArrayList<Reply>>() {}.getType();
            repliesList = new Gson().fromJson(repliesJson, type);
        } else {
            repliesList = new ArrayList<>();
        }

        String token = TokenManager.getSavedToken(getContext());
        HTTPRequest.get("notification/comments/", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("commentsFragment","Network request failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    try{
                        JSONArray jsonArray = new JSONArray(responseData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            JSONObject userProfile = jsonObject.getJSONObject("user_profile");
                            JSONObject user = userProfile.getJSONObject("user");
                            String username = user.getString("username");
                            String avatar = userProfile.getString("avatar");

                            JSONObject post = jsonObject.getJSONObject("post");
                            String postId = post.getString("id");

                            String content = jsonObject.getString("content");
                            repliesList.add(new Reply(avatar,username,content,postId));
                        }

                        // sharedpreference
                        String repliesJson = new Gson().toJson(repliesList);
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("repliesList", repliesJson);
                        editor.apply();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView = view.findViewById(R.id.reply_recycle);
                                adapter = new RepliesAdapter(context,repliesList);
                                recyclerView.setAdapter(adapter);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                                layoutManager.setReverseLayout(true);
                                layoutManager.setStackFromEnd(true);
                                recyclerView.setLayoutManager(layoutManager);
                            }
                        });

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        String currentTime = dateFormat.format(new Date());
                        JSONObject json = new JSONObject();
                        json.put("time", currentTime);

                        HTTPRequest.post("notification/comments-received/", json.toString(), token, new Callback() {
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