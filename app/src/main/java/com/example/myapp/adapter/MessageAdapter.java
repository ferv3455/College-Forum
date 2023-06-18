package com.example.myapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.activity.ChatActivity;
import com.example.myapp.data.ChatSession;
import com.example.myapp.data.Message;
import com.google.gson.Gson;

import java.util.Base64;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<ChatSession> sessions;
    private final LayoutInflater inflater;
    private Context context;
    private ChatEventListener listener;

    public interface ChatEventListener {
        void onEnterChat(int userId, String username);
    }

    public MessageAdapter(Context context, List<ChatSession> sessions, ChatEventListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.sessions = sessions;
        this.listener = listener;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = inflater.inflate(R.layout.message_item, parent, false);
        final MessageViewHolder holder = new MessageViewHolder(view, this);
        holder.itemView.setOnClickListener(v -> {
            int i = holder.getAdapterPosition();
            ChatSession session = sessions.get(i);
            listener.onEnterChat(session.getUserId(), session.getUsername());
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatSession session = sessions.get(position);
        String image = session.getAvatar();
        //Bitmap 解析
        byte[] decodedString = android.util.Base64.decode(image, android.util.Base64.DEFAULT);
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        int unread = session.getUnread();
        String username = sessions.get(position).getUsername();
        Message lastMsg = sessions.get(position).getMessage();

        holder.imageview.setImageBitmap(image_bitmap);
        holder.usnview.setText(username);
        if (lastMsg != null) {
            holder.timeview.setText(lastMsg.getTime());
            holder.message_detail_view.setText(lastMsg.getContent());
        }
        else {
            holder.timeview.setText("");
            holder.message_detail_view.setText("");
        }

        if (unread > 0) {
            holder.message_unread_view.setVisibility(View.VISIBLE);
            holder.message_unread_view.setText(Integer.toString(unread));
        }
        else {
            holder.message_unread_view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

}

class MessageViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageview;
    public final TextView usnview;
    public final TextView timeview;
    public final TextView message_detail_view;
    public final TextView message_unread_view;

    public MessageViewHolder(@NonNull View itemView, MessageAdapter adapter) {
        super(itemView);
        imageview = itemView.findViewById(R.id.message_item_image);
        usnview = itemView.findViewById(R.id.message_item_usn);
        timeview = itemView.findViewById(R.id.message_item_datetime);
        message_detail_view = itemView.findViewById(R.id.message_item_relpy);
        message_unread_view = itemView.findViewById(R.id.message_item_unread);
    }
}
