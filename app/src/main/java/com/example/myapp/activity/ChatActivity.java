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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        titleView = findViewById(R.id.chatActivityTitle);
        recyclerView = findViewById(R.id.chatRecyclerView);
        myInputMessage = findViewById(R.id.sendChatEdit);
        sendMessage = findViewById(R.id.sendChatButton);
        // false 指的是自己发的
        // 从后端获取聊天信息
        List<Message> msgList = new ArrayList<>();
        msgList.add(new Message("this is good", true));
        msgList.add(new Message("this is bad", false));
        msgList.add(new Message("this is zg", false));
        msgList.add(new Message("this is by", false));
        msgList.add(new Message("this is zb", false));
        msgList.add(new Message("this is cqg", false));

        adapter = new ChatHistoryAdapter(this, msgList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String otherUsn = intent.getStringExtra("username");
        titleView.setText(otherUsn);
        adapter.setOtherUsername(otherUsn);
        // 从后端获取头像信息
        HTTPRequest httpRequest = new HTTPRequest();
        String token = TokenManager.getSavedToken(this);
        httpRequest.get("BASE_URL/account/profile",token,new Callback(){
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
                    } catch (JSONException e) {
                        // 处理 JSON 解析异常
                    }
                }
            }
        });

        httpRequest.get("BASE_URL/account/profile/"+otherUsn,token,new Callback(){
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
                        otherProfileImage = BitmapFactory.decodeByteArray(image,0, image.length);
                    } catch (JSONException e) {
                        // 处理 JSON 解析异常
                    }
                }
            }
        });

        adapter.setProfiles(myProfileImage,otherProfileImage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = myInputMessage.getText().toString();
                Message newMessage = new Message(messageText,false);
                adapter.addMessage(newMessage);
                myInputMessage.setText("");
            }
        });
    }
}