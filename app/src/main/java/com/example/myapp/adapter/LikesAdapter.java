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
import com.example.myapp.data.likes;
import com.example.myapp.detail_for_tempory;


import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesViewHolder> {

    private List<com.example.myapp.data.likes> likesList;
    private final LayoutInflater inflater;

    public LikesAdapter(Context context, List<likes> likesList) {
        this.inflater = LayoutInflater.from(context);
        this.likesList = likesList;
    }

    @Override
    public LikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = inflater.inflate(R.layout.like_item, parent, false);
        final LikesViewHolder holder = new LikesViewHolder(view, this);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h = holder.getAdapterPosition();
                likes m = likesList.get(h);
                Context context = view.getContext();
                Intent intent = new Intent(context, detail_for_tempory.class);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
        int image = likesList.get(position).getImage();
        String usn = likesList.get(position).getUsn();
        holder.imageview.setImageResource(image);
        holder.usnview.setText(usn);
    }

    @Override
    public int getItemCount() {
        return likesList.size();
    }

}

class LikesViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageview;
    public final TextView usnview;

    public LikesViewHolder(@NonNull View itemView, LikesAdapter adapter) {
        super(itemView);
        imageview = itemView.findViewById(R.id.like_item_image);
        usnview = itemView.findViewById(R.id.like_item_text);
    }
}

