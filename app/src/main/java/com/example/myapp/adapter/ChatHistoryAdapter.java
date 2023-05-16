package com.example.myapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.data.Message;
import com.example.myapp.data.MessageList;
import com.example.myapp.data.Post;
import com.example.myapp.data.PostList;


class ChatViewHolder extends RecyclerView.ViewHolder {
    public final TextView textView;
    private final ChatHistoryAdapter adapter;

    public ChatViewHolder(@NonNull View itemView, ChatHistoryAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        textView = itemView.findViewById(R.id.message_item);
    }
}


public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private final int[] paddings;
    private final LayoutInflater inflater;
    private final Context context;
    private final MessageList msgList;

    public ChatHistoryAdapter(Context context, MessageList msgList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.msgList = msgList;
        this.paddings = new int[] {
            context.getResources().getDimensionPixelSize(R.dimen.message_padding_vertical),
            context.getResources().getDimensionPixelSize(R.dimen.message_padding_horizontal_long),
            context.getResources().getDimensionPixelSize(R.dimen.message_padding_horizontal_short),
        };
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view
        View mItemView = inflater.inflate(R.layout.message_item, parent, false);
        return new ChatViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message msg = msgList.list().get(position);
        TextView view = holder.textView;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.textView.getLayoutParams();
        view.setText(msg.content);
        if (msg.incoming) {
            view.setBackgroundResource(R.drawable.bubble_in);
            view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            view.setPadding(paddings[1], paddings[0], paddings[2], paddings[0]);
            params.gravity = Gravity.START;
        }
        else {
            view.setBackgroundResource(R.drawable.bubble_out);
            view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_green)));
            view.setPadding(paddings[2], paddings[0], paddings[1], paddings[0]);
            params.gravity = Gravity.END;
        }
    }

    @Override
    public int getItemCount() {
        return msgList.list().size();
    }
}
