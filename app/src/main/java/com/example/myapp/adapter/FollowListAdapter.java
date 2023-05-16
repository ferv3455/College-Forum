package com.example.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.data.Follow;
import com.example.myapp.data.FollowList;

class FollowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final TextView usernameView;
    private final FollowListAdapter adapter;

    public FollowViewHolder(@NonNull View itemView, FollowListAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);
        usernameView = itemView.findViewById(R.id.follower_name);
    }

    @Override
    public void onClick(View view) {
        int position = this.getAdapterPosition();
        adapter.showFollowDetail(position);
    }
}
public class FollowListAdapter extends RecyclerView.Adapter<FollowViewHolder> {
    private final FollowList followList;
    private final LayoutInflater inflater;
    private final Context context;
    private final FollowDetailEventListener listener;

    public interface FollowDetailEventListener {
        void onDisplayFollowDetail(int data);
    }

    public FollowListAdapter(Context context, FollowList followList, FollowDetailEventListener listener) {
        inflater = LayoutInflater.from(context);
        this.followList = followList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view
        View mItemView = inflater.inflate(R.layout.follow_item, parent, false);
        return new FollowViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        Follow follow = followList.get(position);
        // Update other views in the holder based on the Follow object
        holder.usernameView.setText(follow.getFollowerName());
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public void showFollowDetail(int index) {
        listener.onDisplayFollowDetail(index);
    }


}


