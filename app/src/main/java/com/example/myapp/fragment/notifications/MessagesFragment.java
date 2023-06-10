package com.example.myapp.fragment.notifications;

import android.content.Context;
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
import com.example.myapp.adapter.MessageAdapter;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.ChatSession;
import com.example.myapp.data.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessagesFragment extends Fragment {

    private final List<ChatSession> messages = new ArrayList<>(); // 这是要往adapter传的信息，对应我在message页面需要的信息
    private final List<FullMessage> allMessages = new ArrayList<>();  // 这是全部的信息，将他持久化存储就可以了，别的无所谓
    Map<String, String> allimage = new HashMap<>();  // 这是所有图片，可以通过username来获取
    Map<String, List<Message>> allMessageList = new HashMap<>();  // 这是所有人的聊天记录，可以通过username获取
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private String myusn;

    public static class FullMessage{
        private User fromUser;
        private User toUser;
        private String content;
        private String createdAt;

        public FullMessage(User fromUser, User toUser, String content, String createdAt) {
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.content = content;
            this.createdAt = createdAt;
        }

        public User getFromUser() {
            return fromUser;
        }

        public void setFromUser(User fromUser) {
            this.fromUser = fromUser;
        }

        public User getToUser() {
            return toUser;
        }

        public void setToUser(User toUser) {
            this.toUser = toUser;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public static class User {
            private UserProfile user;
            private String avatar;
            private String description;

            // 构造函数、getter和setter方法省略

            public User(UserProfile user, String avatar, String description) {
                this.user = user;
                this.avatar = avatar;
                this.description = description;
            }

            public UserProfile getUser() {
                return user;
            }

            public void setUser(UserProfile user) {
                this.user = user;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            // 内部类表示用户详细信息
            public static class UserProfile {
                private int id;
                private String username;
                // 构造函数、getter和setter方法省略

                public UserProfile(int id, String username) {
                    this.id = id;
                    this.username = username;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getUsername() {
                    return username;
                }

                public void setUsername(String username) {
                    this.username = username;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_notifications_messages, container, false);
        // shared preference


        String token = TokenManager.getSavedToken(getContext());

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
                        myusn = userObject.getString("username");
                    } catch (JSONException e) {
                        // 处理 JSON 解析异常
                    }
                }
            }
        });

        HTTPRequest.get("notification/messages/", token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MessageFragment","Network request failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        // 将返回的 JSON 数据解析为 FullMessage 类型的列表
                        JSONArray jsonArray = new JSONArray(responseData);
                        // 更新消息列表
                        parseFullMessages(responseData);
                        updateAllMessageList(allMessages);
                        updateimage(allMessages);
                        updatesession(allMessageList,allimage);

                        // sharedpreference

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        String currentTime = dateFormat.format(new Date());
                        JSONObject json = new JSONObject();
                        json.put("time", currentTime);

                        HTTPRequest.post("notification/messages-received/", json.toString(), token, new Callback() {
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
                                recyclerView = view.findViewById(R.id.message_recycle);
                                adapter = new MessageAdapter(context,messages);
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

    private void parseFullMessages(String responseData) throws JSONException {
        JSONArray jsonArray = new JSONArray(responseData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonMessage = jsonArray.getJSONObject(i);
            JSONObject jsonFromUser = jsonMessage.getJSONObject("fromUser");
            JSONObject jsonToUser = jsonMessage.getJSONObject("toUser");

            FullMessage.User.UserProfile fromUserProfile = new FullMessage.User.UserProfile(
                    jsonFromUser.getJSONObject("user").getInt("id"),
                    jsonFromUser.getJSONObject("user").getString("username")
            );
            FullMessage.User.UserProfile toUserProfile = new FullMessage.User.UserProfile(
                    jsonToUser.getJSONObject("user").getInt("id"),
                    jsonToUser.getJSONObject("user").getString("username")
            );

            FullMessage.User fromUser = new FullMessage.User(
                    fromUserProfile,
                    jsonFromUser.getString("avatar"),
                    jsonFromUser.getString("description")
            );
            FullMessage.User toUser = new FullMessage.User(
                    toUserProfile,
                    jsonToUser.getString("avatar"),
                    jsonToUser.getString("description")
            );

            FullMessage fullMessage = new FullMessage(
                    fromUser,
                    toUser,
                    jsonMessage.getString("content"),
                    jsonMessage.getString("createdAt")
            );

            allMessages.add(fullMessage);
        }
    }

    private void updateAllMessageList (List<FullMessage> fullMessages) {
        for (FullMessage fullMessage:fullMessages) {
            String fromUserUsername = fullMessage.getFromUser().getUser().getUsername();
            if (!allMessageList.containsKey(fromUserUsername)) {
                allMessageList.put(fromUserUsername, new ArrayList<>());
            }
            List<Message> fromUserMessages = allMessageList.get(fromUserUsername);
            if(fromUserUsername!=myusn)
                fromUserMessages.add(new Message(fullMessage.getContent(), true));
            else fromUserMessages.add(new Message(fullMessage.getContent(), false));
        }
    }

    private void updateimage (List<FullMessage> fullMessages) {
        for (FullMessage message: fullMessages) {
            String usn1 = message.getFromUser().getUser().getUsername();
            if (usn1!=myusn) {
                if (!allimage.containsKey(usn1)) {
                    String avatar = message.getFromUser().getAvatar();
                    allimage.put(usn1,avatar);
                }
            }
            String usn = message.getToUser().getUser().getUsername();
            if (usn!=myusn) {
                if (!allimage.containsKey(usn)) {
                    String avatar = message.getToUser().getAvatar();
                    allimage.put(usn,avatar);
                }
            }

        }
    }

    private void updatesession (Map<String, List<Message>> allMessageList, Map<String, String>allImage) {
        // ChatSession(String image, String usn, String message, List<Message> chathistory)
        // messages
        for (String username : allImage.keySet()) {
            List<Message> chathistory = allMessageList.get(username);
            String image = allImage.get(username);
            String lastmessage = chathistory.get(chathistory.size()-1).content;
            if (chathistory!=null) {
                ChatSession chatSession = new ChatSession(image,username,lastmessage,chathistory);
                messages.add(chatSession);
            }
        }
    }


}