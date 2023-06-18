package com.example.myapp.fragment.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.R;
import com.example.myapp.activity.ChatActivity;
import com.example.myapp.activity.MainActivity;
import com.example.myapp.activity.NewPostActivity;
import com.example.myapp.adapter.LikesAdapter;
import com.example.myapp.adapter.MessageAdapter;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.ChatSession;
import com.example.myapp.data.Like;
import com.example.myapp.data.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessagesFragment extends Fragment implements MessageAdapter.ChatEventListener {
    private static final String PREF_FILE ="com.example.myapp.messageSharedPrefs";
    private static final String SESSION_LIST = "SESSION_LIST";
    private static final String AVATAR = "AVATAR";

    private List<ChatSession> sessions;

    private Context context;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private String avatar;

    private final ActivityResultLauncher<Intent> chatLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> updateSessionsFromStorage()
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications_messages, container, false);

        refreshLayout = view.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this::updateMessages);

        // Get saved session list from shared preference
        context = getContext();
        assert context != null;
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String sessionJson = sharedPref.getString(SESSION_LIST, "");
        avatar = sharedPref.getString(AVATAR, "");
//        Log.d("list", sessionJson);

        if (!sessionJson.equals("")) {
            Type type = new TypeToken<ArrayList<ChatSession>>() {}.getType();
            sessions = new Gson().fromJson(sessionJson, type);
//            Log.d("list", String.valueOf(sessions.size()));
        } else {
            sessions = new ArrayList<>();
        }

        // Recycler view
        recyclerView = view.findViewById(R.id.message_recycle);
        adapter = new MessageAdapter(context, sessions, this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Update personal information (useful in session list update)
        HTTPRequest.get("account/profile", TokenManager.getSavedToken(context), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String newAvatar = jsonObject.getString("avatar");

                        if (!newAvatar.equals(avatar)) {
                            avatar = newAvatar;
                            SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor preferencesEditor = preferences.edit();
                            preferencesEditor.putString(AVATAR, newAvatar);
                            preferencesEditor.apply();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        updateMessages();
        return view;
    }

    public void updateMessages() {
        String token = TokenManager.getSavedToken(getContext());
        HTTPRequest.get("notification/messages/", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MessageFragment","Network request failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                    String responseData = response.body().string();
                    try {
                        // 将返回的 JSON 数据解析为 FullMessage 类型的列表
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            // 更新消息列表
                            updateSessions(jsonArray);
                            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

                            // Shared Preference
                            String sessionsJson = new Gson().toJson(sessions);
//                            Log.d("save", sessionsJson);
                            SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor preferencesEditor = preferences.edit();
                            preferencesEditor.putString(SESSION_LIST, sessionsJson);
                            preferencesEditor.apply();

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                            TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
                            formatter.setTimeZone(tz);
                            String currentTime = formatter.format(new Date());
                            JSONObject json = new JSONObject();
                            json.put("time", currentTime);

                            HTTPRequest.post("notification/messages-received/", json.toString(), token, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e("Receive Message", "Failed to post time: " + e.getMessage(), e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                        throw new IOException("Unexpected code " + response);
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        // 处理 JSON 解析异常
                        e.printStackTrace();
                    }
                } else {
                    // 处理请求失败的情况
                    getActivity().runOnUiThread(() -> refreshLayout.setRefreshing(false));
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    private void updateSessions(JSONArray messageList) throws JSONException {
        // Insert messages
        for (int i = 0; i < messageList.length(); i++) {
            JSONObject jsonMessage = messageList.getJSONObject(i);
            JSONObject jsonFromUser = jsonMessage.getJSONObject("fromUser");

            Message newMsg = new Message(jsonMessage.getString("content"), true,
                    jsonMessage.getString("createdAt"));

            int userId = jsonFromUser.getJSONObject("user").getInt("id");
            String username = jsonFromUser.getJSONObject("user").getString("username");
            String avatar = jsonFromUser.getString("avatar");

            ChatSession session = findSession(userId);
            if (session == null) {
                session = new ChatSession(userId, avatar, username, new ArrayList<>());
                sessions.add(session);
            }

            ((ArrayList<Message>) session.getChatHistory()).add(0, newMsg);
            session.incrUnread();
        }

        // Sort sessions
        Collections.sort(sessions);
    }

    private ChatSession findSession(int userId) {
        for (ChatSession session : sessions) {
            if (session.getUserId() == userId) {
                return session;
            }
        }
        return null;
    }

    private void updateSessionsFromStorage() {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String sessionJson = sharedPref.getString(SESSION_LIST, "");
        avatar = sharedPref.getString(AVATAR, "");
        ArrayList<ChatSession> newSessions;
        Log.d("new session", sessionJson);

        if (!sessionJson.equals("")) {
            Type type = new TypeToken<ArrayList<ChatSession>>() {}.getType();
            newSessions = new Gson().fromJson(sessionJson, type);
        } else {
            newSessions = new ArrayList<>();
        }

        sessions.clear();
        sessions.addAll(newSessions);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEnterChat(int userId, String username) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.USERID, userId);
        intent.putExtra(ChatActivity.USERNAME, username);
        chatLauncher.launch(intent);
    }
}