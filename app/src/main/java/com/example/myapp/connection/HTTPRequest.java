package com.example.myapp.connection;

import android.util.Log;

import java.io.File;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    public static void getWithParams(String url, Map<String, String> params, String token, Callback callback) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(BASE_URL + url).newBuilder();
        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(),param.getValue());
            }
        }

        Request request;
        if (token != null) {
            request = new Request.Builder()
                    .url(httpBuilder.build())
                    .addHeader("Authorization", String.format("Token %s", token))
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(httpBuilder.build())
                    .build();
        }
        client.newCall(request).enqueue(callback);
        Log.d("HTTP", "attempting to send a GET request with params");
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

    public static void post(String url, File file, String token, Callback callback) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/png")))
                .build();
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
        Log.d("HTTP", "attempting to send a POST request with files");
    }

    public static void put(String url, String json, String token, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request;
        if (token != null) {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .put(body)
                    .addHeader("Authorization", String.format("Token %s", token))
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .put(body)
                    .build();
        }
        client.newCall(request).enqueue(callback);
        Log.d("HTTP", "attempting to send a PUT request");
    }

    public static void delete(String url, String json, String token, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request;
        if (token != null) {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .delete(body)
                    .addHeader("Authorization", String.format("Token %s", token))
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(BASE_URL + url)
                    .delete(body)
                    .build();
        }
        client.newCall(request).enqueue(callback);
        Log.d("HTTP", "attempting to send a DELETE request");
    }

}
