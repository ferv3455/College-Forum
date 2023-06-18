package com.example.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.activity.ChatActivity;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.data.ChatSession;
import com.example.myapp.data.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.BitSet;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private final ChatSession session;
    private String avatar;
    private static final int IS_MY_MESSAGE = 1;
    private static final int IS_OTHERS_MESSAGE = 2;
//    private String otherUsername;
//    private Bitmap myProfile;
//    private Bitmap otherProfile;
//    private String myUsername;

    public ChatHistoryAdapter(Context context, ChatSession session, String avatar) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.session = session;
        this.avatar = avatar;
    }

    private static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        public final TextView myTextView;
        public final ImageView myImage;
        public MyMessageViewHolder(@NonNull View itemView, ChatHistoryAdapter adapter) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.mine_bubble);
            myImage = itemView.findViewById(R.id.mine_profile);
        }
    }

    private static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        public final TextView otherTextView;
        public final ImageView otherImage;
        public OtherMessageViewHolder(@NonNull View itemView, ChatHistoryAdapter adapter) {
            super(itemView);
            otherTextView = itemView.findViewById(R.id.other_bubble);
            otherImage = itemView.findViewById(R.id.other_profile);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view
        if (viewType == IS_MY_MESSAGE) {
            View mItemView = inflater.inflate(R.layout.bubble_item_mine, parent, false);
            return new MyMessageViewHolder(mItemView, this);
        } else {
            View mItemView = inflater.inflate(R.layout.bubble_item_others, parent, false);
            return new OtherMessageViewHolder(mItemView, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = session.getChatHistory().get(position);
        if (holder instanceof MyMessageViewHolder) {
            MyMessageViewHolder myHolder = (MyMessageViewHolder) holder;

            byte[] image = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                image = Base64.getDecoder().decode(avatar);
            }
            Bitmap profile = BitmapFactory.decodeByteArray(image,0, image.length);

            myHolder.myTextView.setText(msg.getContent());
            myHolder.myImage.setImageBitmap(profile);
        }
        else if (holder instanceof OtherMessageViewHolder){
            OtherMessageViewHolder otherHolder = (OtherMessageViewHolder) holder;

            byte[] image = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                image = Base64.getDecoder().decode(session.getAvatar());
            }
            Bitmap profile = BitmapFactory.decodeByteArray(image,0, image.length);

            otherHolder.otherTextView.setText(msg.getContent());
            otherHolder.otherImage.setImageBitmap(profile);
        }
    }

    @Override
    public int getItemCount() {
        return session.getChatHistory().size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = session.getChatHistory().get(position);
        if (message.isIncoming() == false) {
            return IS_MY_MESSAGE;
        }
        else {
            return IS_OTHERS_MESSAGE;
        }
    }

    public void scrollToBottom(RecyclerView recyclerView) {
        int position = session.getChatHistory().size() - 1;
        recyclerView.scrollToPosition(position);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
