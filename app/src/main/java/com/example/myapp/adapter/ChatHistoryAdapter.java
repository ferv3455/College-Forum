package com.example.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.data.Message;

import java.util.BitSet;
import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    Context context;
    private final List<Message> msgList;
    private static final  int IS_MY_MESSAGE = 1;
    private static final  int IS_OTHERS_MESSAGE = 2;
    private String otherUsername;
    private Bitmap myProfile;
    private Bitmap otherProfile;

    public ChatHistoryAdapter(Context context, List<Message> msgList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.msgList = msgList;
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
            return new MyMessageViewHolder (mItemView,this);
        } else if (viewType == IS_OTHERS_MESSAGE) {
            View mItemView = inflater.inflate(R.layout.bubble_item_others, parent, false);
            return new OtherMessageViewHolder(mItemView, this);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = msgList.get(position);
        if (holder instanceof MyMessageViewHolder) {
            MyMessageViewHolder myHolder = (MyMessageViewHolder) holder;
            myHolder.myTextView.setText(msg.getContent());
            myHolder.myImage.setImageBitmap(myProfile);
        }
        else if (holder instanceof OtherMessageViewHolder){
            OtherMessageViewHolder otherHolder = (OtherMessageViewHolder) holder;
            otherHolder.otherTextView.setText(msg.getContent());
            otherHolder.otherImage.setImageBitmap(otherProfile);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = msgList.get(position);
        if (message.isIncoming() == false) {
            return IS_MY_MESSAGE;
        }
        else {
            return IS_OTHERS_MESSAGE;
        }
    }
    public void scrollToBottom(RecyclerView recyclerView) {
        int position = msgList.size() - 1;
        recyclerView.scrollToPosition(position);
    }
    public  void  addMessage(Message message, RecyclerView recyclerView) {
        msgList.add(message);
        notifyDataSetChanged();
        scrollToBottom(recyclerView);
    }
    public void setOtherUsername(String usn) {
        this.otherUsername = usn;
    }
    public void setProfiles (Bitmap myProfile, Bitmap otherProfile){
        this.myProfile = myProfile;
        this.otherProfile = otherProfile;
    }
}
