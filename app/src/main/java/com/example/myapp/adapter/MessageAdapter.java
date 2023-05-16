package com.example.myapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.chat_for_temporary;
import com.example.myapp.data.messages;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<messages> messages;
    private final LayoutInflater inflater;

    public MessageAdapter(Context context, List<messages> messages) {
        this.inflater = LayoutInflater.from(context);
        this.messages = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = inflater.inflate(R.layout.message_item, parent, false);
        final MessageViewHolder holder = new MessageViewHolder(view, this);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h = holder.getAdapterPosition();
                com.example.myapp.data.messages m = messages.get(h);
                Context context = view.getContext();
                Intent intent = new Intent(context, chat_for_temporary.class);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        int image = messages.get(position).getImage();
        String usn = messages.get(position).getUsn();
        String reply = messages.get(position).getMessage();
        holder.imageview.setImageResource(image);
        holder.usnview.setText(usn);
        holder.message_detail_view.setText(reply);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}

class MessageViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageview;
    public final TextView usnview;
    public final TextView message_detail_view;

    public MessageViewHolder(@NonNull View itemView, MessageAdapter adapter) {
        super(itemView);
        imageview = itemView.findViewById(R.id.message_item_image);
        usnview = itemView.findViewById(R.id.message_item_usn);
        message_detail_view = itemView.findViewById(R.id.message_item_relpy);
    }
}
