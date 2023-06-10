package com.example.myapp.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.myapp.activity.DetailActivity;
import com.example.myapp.data.Reply;


import java.util.Base64;
import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesViewHolder> {

    private List<Reply> repliesList;
    private final LayoutInflater inflater;

    public RepliesAdapter(Context context, List<Reply> repliesList) {
        this.inflater = LayoutInflater.from(context);
        this.repliesList = repliesList;
    }

    @Override
    public RepliesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = inflater.inflate(R.layout.reply_item, parent, false);
        final RepliesViewHolder holder = new RepliesViewHolder(view, this);
        holder.itemView.setOnClickListener(v -> {
            int h = holder.getAdapterPosition();
            Reply m = repliesList.get(h);
            String id = m.getId();
            Context context = view.getContext();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", id);
            context.startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RepliesViewHolder holder, int position) {
        String image = repliesList.get(position).getImage();
        String usn = repliesList.get(position).getUsn();
        String reply = repliesList.get(position).getReply();
        byte[] image1 = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            image1 = Base64.getDecoder().decode(image);
        }
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(image1,0, image1.length);
        holder.imageview.setImageBitmap(image_bitmap);
        holder.usnview.setText(usn);
        holder.replyview.setText(reply);
    }

    @Override
    public int getItemCount() {
        return repliesList.size();
    }

}

class RepliesViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageview;
    public final TextView usnview;
    public final TextView replyview;

    public RepliesViewHolder(@NonNull View itemView, RepliesAdapter adapter) {
        super(itemView);
        imageview = itemView.findViewById(R.id.reply_item_image);
        usnview = itemView.findViewById(R.id.reply_item_usn);
        replyview = itemView.findViewById(R.id.reply_item_relpy);
    }
}

