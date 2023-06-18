package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.adapter.ChatHistoryAdapter;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.ChatSession;
import com.example.myapp.data.Message;
import com.example.myapp.connection.HTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private static final String PREF_FILE ="com.example.myapp.messageSharedPrefs";
    private static final String SESSION_LIST = "SESSION_LIST";
    private static final String AVATAR = "AVATAR";

    public static final String USERID = "USERID";
    public static final String USERNAME = "USERNAME";

    private List<ChatSession> sessions;
    private ChatSession session;
    private String avatar;

    TextView titleView;
    EditText myInputMessage;
    Button sendMessage;

    RecyclerView recyclerView;
    ChatHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get session list
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String sessionJson = sharedPref.getString(SESSION_LIST, "");
        avatar = sharedPref.getString(AVATAR, "");

        if (!sessionJson.equals("")) {
            Type type = new TypeToken<ArrayList<ChatSession>>() {}.getType();
            sessions = new Gson().fromJson(sessionJson, type);
        } else {
            sessions = new ArrayList<>();
        }

        // Get user id
        Intent intent = getIntent();
        int userId = intent.getIntExtra(USERID, -1);
        String username = intent.getStringExtra(USERNAME);
        assert userId >= 0;

        // Get session (add if not exists)
        session = findSession(userId);
        if (session == null) {
            session = new ChatSession(userId, "", username, new ArrayList<>());
            sessions.add(session);
        }

        session.resetUnread();

        // Views
        titleView = findViewById(R.id.chatActivityTitle);
        recyclerView = findViewById(R.id.chatRecyclerView);
        myInputMessage = findViewById(R.id.sendChatEdit);
        sendMessage = findViewById(R.id.sendChatButton);

        titleView.setText(username);

        // false 指的是自己发的
        // 从后端获取聊天信息
        adapter = new ChatHistoryAdapter(this, session, avatar);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom && adapter.getItemCount() > 0) {
                recyclerView.postDelayed(() -> recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1), 100);
            }
        });

        sendMessage.setOnClickListener(v -> {
            String messageText = myInputMessage.getText().toString();
            JSONObject params = new JSONObject();
            try {
                params.put("user", session.getUsername());
                params.put("content", messageText);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            HTTPRequest.post("notification/messages/", params.toString(), TokenManager.getSavedToken(this), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }

                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        try {
                            // 将返回的 JSON 数据解析为 FullMessage 类型的列表
                            JSONObject jsonObject = new JSONObject(responseData);
                            String time = jsonObject.getString("time");
                            Message newMessage = new Message(messageText, false, time);
                            session.getChatHistory().add(0, newMessage);

                            runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                adapter.scrollToBottom(recyclerView);
                                myInputMessage.setText("");
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        });

        HTTPRequest.get("account/profile", TokenManager.getSavedToken(this), new Callback() {
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
                            SharedPreferences preferences = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor preferencesEditor = preferences.edit();
                            preferencesEditor.putString(AVATAR, newAvatar);
                            preferencesEditor.apply();
                        }

                        if (adapter != null) {
                            runOnUiThread(() -> {
                                adapter.setAvatar(avatar);
                                adapter.notifyDataSetChanged();
                                adapter.scrollToBottom(recyclerView);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        HTTPRequest.get("account/profile/" + username, TokenManager.getSavedToken(this), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String newOppAvatar = jsonObject.getString("avatar");

                        if (!newOppAvatar.equals(session.getAvatar())) {
                            session.setAvatar(newOppAvatar);
                        }

                        if (adapter != null) {
                            runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                adapter.scrollToBottom(recyclerView);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Collections.sort(sessions);

        String sessionsJson = new Gson().toJson(sessions);
        SharedPreferences preferences = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(SESSION_LIST, sessionsJson);
        preferencesEditor.apply();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private ChatSession findSession(int userId) {
        for (ChatSession session : sessions) {
            if (session.getUserId() == userId) {
                return session;
            }
        }
        return null;
    }
}