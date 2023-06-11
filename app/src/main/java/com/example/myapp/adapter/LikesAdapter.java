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
import com.example.myapp.data.Like;

import java.util.Base64;
import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesViewHolder> {

    private List<Like> likesList;
    private final LayoutInflater inflater;

    public LikesAdapter(Context context, List<Like> likesList) {
        this.inflater = LayoutInflater.from(context);
        this.likesList = likesList;
    }

    @Override
    public LikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = inflater.inflate(R.layout.like_item, parent, false);
        final LikesViewHolder holder = new LikesViewHolder(view, this);
        holder.itemView.setOnClickListener(v -> {
            int h = holder.getAdapterPosition();
            Like m = likesList.get(h);
            String id = m.getId();
            Context context = view.getContext();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", id);
            context.startActivity(intent);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikesViewHolder holder, int position) {
        Like like = likesList.get(position);

        String image = like.getImage();
        byte[] image1 = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            image1 = Base64.getDecoder().decode(image);
        }
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(image1,0, image1.length);

        holder.imageview.setImageBitmap(image_bitmap);
        holder.usnview.setText(like.getUsn());
        holder.timeview.setText(like.getTime());
        holder.titleview.setText(like.getTitle());
    }

    @Override
    public int getItemCount() {
        return likesList.size();
    }

}

class LikesViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageview;
    public final TextView usnview;
    public final TextView timeview;
    public final TextView titleview;

    public LikesViewHolder(@NonNull View itemView, LikesAdapter adapter) {
        super(itemView);
        imageview = itemView.findViewById(R.id.avatar);
        usnview = itemView.findViewById(R.id.username);
        timeview = itemView.findViewById(R.id.datetime);
        titleview = itemView.findViewById(R.id.postTitle);
    }
}

