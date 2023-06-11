package com.example.myapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.adapter.ChatHistoryAdapter;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.Message;
import com.example.myapp.connection.HTTPRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    TextView titleView;
    EditText myInputMessage;
    Button sendMessage;
    RecyclerView recyclerView;
    ChatHistoryAdapter adapter;
    Bitmap otherProfileImage;
    Bitmap myProfileImage;
    String myusn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        String chatHistoryJson = bundle.getString("chatHistoryJson");
        String username = bundle.getString("username");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        ArrayList<Message> chatHistory = gson.fromJson(chatHistoryJson, listType);

        titleView = findViewById(R.id.chatActivityTitle);
        recyclerView = findViewById(R.id.chatRecyclerView);
        myInputMessage = findViewById(R.id.sendChatEdit);
        sendMessage = findViewById(R.id.sendChatButton);
        // false 指的是自己发的
        // 从后端获取聊天信息
        List<Message> msgList = chatHistory;

        adapter = new ChatHistoryAdapter(this, msgList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
        titleView.setText(username);
        adapter.setOtherUsername(username);

        String token = TokenManager.getSavedToken(this);
        HTTPRequest.get("account/profile",token,new Callback(){
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 处理请求失败的情况
                } else {
                    String responseData = response.body().string();
                    try {
                        // 将返回的 JSON 数据解析为对象
                        JSONObject jsonObject = new JSONObject(responseData);
                        // 获取 user 字段的值
                        JSONObject userObject = jsonObject.getJSONObject("user");
                        int id = userObject.getInt("id");
                        String username = userObject.getString("username");
                        String avatar = jsonObject.getString("avatar");
                        // 对解析后的数据进行处理
                        byte[] image = new byte[0];
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            image = Base64.getDecoder().decode(avatar);
                        }
                        myProfileImage = BitmapFactory.decodeByteArray(image,0, image.length);
                        HTTPRequest.get("account/profile/"+username,token,new Callback(){
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                            }

                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                    // 处理请求失败的情况
                                } else {
                                    String responseData = response.body().string();
                                    try {
                                        // 将返回的 JSON 数据解析为对象
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        // 获取 user 字段的值
                                        JSONObject userObject = jsonObject.getJSONObject("user");
                                        int id = userObject.getInt("id");
                                        myusn = userObject.getString("username");
                                        adapter.setmyUsername(myusn);
                                        String avatar = jsonObject.getString("avatar");
                                        // 对解析后的数据进行处理
                                        byte[] image = new byte[0];
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            image = Base64.getDecoder().decode(avatar);
                                        }
                                        otherProfileImage = BitmapFactory.decodeByteArray(image,0, image.length);
                                        adapter.setProfiles(myProfileImage,otherProfileImage);
                                    } catch (JSONException e) {
                                        // 处理 JSON 解析异常
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        // 处理 JSON 解析异常
                    }
                }
            }
        });





        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = myInputMessage.getText().toString();
                Message newMessage = new Message(messageText,false, "now");
                try {
                    adapter.addMessage(newMessage,recyclerView);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                myInputMessage.setText("");
            }
        });
    }
}