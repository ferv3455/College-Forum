package com.example.myapp.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Encoder {
    public static String encodeFromUri(ContentResolver resolver, Uri uri, int quality) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            byte[] bytes = stream.toByteArray();
            return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
