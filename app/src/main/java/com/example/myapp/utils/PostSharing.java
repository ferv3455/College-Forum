package com.example.myapp.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class PostSharing {
    public static void sharePost(String id, String title, Context context, View baseView) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Post Link", getSharedText(id, title));
        clipboard.setPrimaryClip(clip);
        Snackbar.make(baseView, "链接已复制", 20).show();
    }

    private static String getSharedText(String id, String title) {
        return "【" + title + "】 http://139.196.30.181:10243/preview/" + id + " - 校园资讯分享平台";
    }
}
