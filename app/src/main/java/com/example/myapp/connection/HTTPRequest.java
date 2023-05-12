package com.example.myapp.connection;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HTTPRequest {
    private static final String BASE_URL = "http://139.196.30.181:10243/";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static void get(String url, String token, Callback callback) {
        Request request;
        if (token != null) {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .addHeader("Authorization", String.format("Token %s", token))
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .build();
        }
        client.newCall(request).enqueue(callback);
        Log.d("HTTP", "attempting to send a GET request");
    }

    public static void post(String url, String json, String token, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request;
        if (token != null) {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .post(body)
                    .addHeader("Authorization", String.format("Token %s", token))
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .post(body)
                    .build();
        }
        client.newCall(request).enqueue(callback);
        Log.d("HTTP", "attempting to send a POST request");
    }
}