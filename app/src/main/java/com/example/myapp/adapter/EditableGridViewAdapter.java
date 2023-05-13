package com.example.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.myapp.R;
import com.example.myapp.activity.NewPostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class EditableGridViewAdapter extends BaseAdapter {
    private String[] images;
    private final LayoutInflater inflater;
    private final Context context;

    public EditableGridViewAdapter(Context context, String[] images) {
        inflater = LayoutInflater.from(context);
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length + 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i == images.length) {
            // Add button
            view = inflater.inflate(R.layout.add_image_item, viewGroup, false); // inflate the layout
            ShapeableImageView button = view.findViewById(R.id.add_image_button);
            button.setOnClickListener(v -> ((NewPostActivity) context).addImage(v));
            return view;
        }
        else {
            // Images
            view = inflater.inflate(R.layout.editable_image_item, viewGroup, false); // inflate the layout

            FloatingActionButton button = view.findViewById(R.id.editable_remove_button);
            button.setOnClickListener(v -> ((NewPostActivity) context).removeImage(i));

            ImageView icon = view.findViewById(R.id.editable_image); // get the reference of ImageView
            byte[] decodedString = Base64.decode(images[i], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            icon.setImageBitmap(decodedByte);
            return view;
        }
    }

    public void setImages(String[] images) {
        this.images = images;
        notifyDataSetChanged();
    }
}
