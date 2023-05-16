package com.example.myapp.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.myapp.R;
import com.example.myapp.adapter.EditableGridViewAdapter;
import com.example.myapp.connection.ContentManager;
import com.example.myapp.data.Post;
import com.example.myapp.utils.Base64Encoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NewPostActivity extends AppCompatActivity {
    public static final String NEW_POST = "NEW_POST";
    private static final String TITLE = "TITLE_TEXT";
    private static final String CONTENT = "CONTENT_TEXT";
    private static final String IMAGE_COUNT = "IMAGE_COUNT";
    private static final String INDEXED_ID = "ID_%d";
    private static final String INDEXED_IMAGE = "IMAGE_%d";

    private EditText titleEdit;
    private EditText contentEdit;
    private GridView imagesView;
    private Button submitButton;
    private EditableGridViewAdapter adapter;

    private ArrayList<String> newImagesId = new ArrayList<>();
    private ArrayList<String> newImagesThumbnail = new ArrayList<>();

    private SharedPreferences preferences;
    private String sharedPrefFile ="com.example.myapp.newPostSharedPrefs";

    private final ActivityResultLauncher<Intent> newImageLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                Intent data = result.getData();
                Uri selectedImageUri = data.getData();
                String encodedImage = Base64Encoder.encodeFromUri(getContentResolver(),
                        selectedImageUri, 100);
                ContentManager.uploadImage(this, encodedImage, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }

                            assert responseBody != null;
                            JSONObject result = new JSONObject(responseBody.string());
                            newImagesId.add(result.getString("id"));
                            newImagesThumbnail.add(result.getString("thumbnail"));
                            if (adapter != null) {
                                runOnUiThread(() -> adapter.setImages(newImagesThumbnail.toArray(new String[newImagesThumbnail.size()])));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
        imagesView = findViewById(R.id.newImagesGridView);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this::submitPost);

        findViewById(R.id.newLocationLayout).setOnClickListener(v -> Log.d("New Post", "Location"));
        findViewById(R.id.newTagLayout).setOnClickListener(v -> Log.d("New Post", "Tag"));

        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        titleEdit.setText(preferences.getString(TITLE, ""));
        contentEdit.setText(preferences.getString(CONTENT, ""));
        int imageCount = preferences.getInt(IMAGE_COUNT, 0);
        for (int i = 0; i < imageCount; i++) {
            newImagesId.add(preferences.getString(String.format(INDEXED_ID, i), "ERROR"));
            newImagesThumbnail.add(preferences.getString(String.format(INDEXED_IMAGE, i), "ERROR"));
        }

        adapter = new EditableGridViewAdapter(this, newImagesThumbnail.toArray(new String[newImagesThumbnail.size()]));
        imagesView.setAdapter(adapter);
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        newImageLauncher.launch(intent);
    }

    public void removeImage(int i) {
        newImagesId.remove(i);
        newImagesThumbnail.remove(i);
        adapter.setImages(newImagesThumbnail.toArray(new String[newImagesThumbnail.size()]));
    }

    public void submitPost(View view) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", titleEdit.getText().toString());
            obj.put("content", contentEdit.getText().toString());
            JSONArray imageArray = new JSONArray(newImagesId.toArray(new String[newImagesId.size()]));
            obj.put("images", imageArray);
            JSONArray tagArray = new JSONArray(new String[]{"Activity"});
            obj.put("tags", tagArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ContentManager.createPost(this, obj, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    assert responseBody != null;
                    newImagesThumbnail.clear();
                    runOnUiThread(() -> {
                        titleEdit.setText("");
                        contentEdit.setText("");
                        newImagesThumbnail.clear();
                        Intent replyIntent = new Intent();
                        setResult(RESULT_OK, replyIntent);
                        finish();
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(TITLE, titleEdit.getText().toString());
        preferencesEditor.putString(CONTENT, contentEdit.getText().toString());
        preferencesEditor.putInt(IMAGE_COUNT, newImagesThumbnail.size());
        for (int i = 0; i < newImagesThumbnail.size(); i++) {
            preferencesEditor.putString(String.format(INDEXED_ID, i), newImagesId.get(i).toString());
            preferencesEditor.putString(String.format(INDEXED_IMAGE, i), newImagesThumbnail.get(i).toString());
        }
        preferencesEditor.apply();
    }
}