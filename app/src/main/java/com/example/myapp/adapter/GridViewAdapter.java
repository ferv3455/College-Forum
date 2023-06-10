package com.example.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.myapp.R;

public class GridViewAdapter extends BaseAdapter {
    private String[] images;
    private final LayoutInflater inflater;
    private final Context context;

    public GridViewAdapter(Context context, String[] images) {
        inflater = LayoutInflater.from(context);
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
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
        view = inflater.inflate(R.layout.image_item, viewGroup, false); // inflate the layout
        ImageView icon = view.findViewById(R.id.image); // get the reference of ImageView
//        icon.setImageURI(images[i]); // set images

        byte[] decodedString = Base64.decode(images[i], Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        icon.setImageBitmap(decodedByte);

        if (context instanceof DisplayMedia) {
            view.setOnClickListener(v -> ((DisplayMedia) context).show(i));
        }

        return view;
    }

    public void setImages(String[] images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public interface DisplayMedia {
        public void show(int i);
    }
}
