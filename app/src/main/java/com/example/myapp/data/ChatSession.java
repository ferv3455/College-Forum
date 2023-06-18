package com.example.myapp.data;

import java.util.List;

public class ChatSession implements Comparable<ChatSession> {
    int userId;
    String avatar;
    String username;
    List<Message> chatHistory;
    int unread;

    public ChatSession(int userId, String avatar, String username, List<Message> chatHistory) {
        this.userId = userId;
        this.avatar = avatar;
        this.username = username;
        this.chatHistory = chatHistory;
        this.unread = 1;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Message getMessage() {
        if (chatHistory != null && chatHistory.size() > 0) {
            return chatHistory.get(0);
        }
        return null;
    }

    public List<Message> getChatHistory() {
        return chatHistory;
    }

    public int getUnread() {
        return unread;
    }

    public void incrUnread() {
        this.unread++;
    }

    public void resetUnread() {
        this.unread = 0;
    }

    @Override
    public int compareTo(ChatSession chatSession) {
        return this.getMessage().getTime().compareTo(chatSession.getMessage().getTime());
    }
}
