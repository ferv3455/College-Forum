package com.example.myapp.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.LinearLayout;

import com.example.myapp.R;
import com.example.myapp.adapter.EditableGridViewAdapter;
import com.example.myapp.data.Post;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {
    public static final String NEW_POST = "NEW_POST";
    private static final String TITLE = "TITLE_TEXT";
    private static final String CONTENT = "CONTENT_TEXT";
    private static final String IMAGE_COUNT = "IMAGE_COUNT";
    private static final String INDEXED_IMAGE = "IMAGE_%d";

    private EditText titleEdit;
    private EditText contentEdit;
    private GridView imagesView;
    private Button submitButton;
    private EditableGridViewAdapter adapter;

    private ArrayList<String> newImages = new ArrayList<>();

    private SharedPreferences preferences;
    private String sharedPrefFile ="com.example.myapp.newPostSharedPrefs";

    private final ActivityResultLauncher<Intent> newImageLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == MainActivity.RESULT_OK) {
                Intent data = result.getData();
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                    byte[] bytes = stream.toByteArray();
                    newImages.add(Base64.encodeToString(bytes, Base64.DEFAULT));
                    adapter.setImages(newImages.toArray(new String[newImages.size()]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            newImages.add(preferences.getString(String.format(INDEXED_IMAGE, i), "ERROR"));
        }

        adapter = new EditableGridViewAdapter(this, newImages.toArray(new String[newImages.size()]));
        imagesView.setAdapter(adapter);
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        newImageLauncher.launch(intent);
    }

    public void removeImage(int i) {
        newImages.remove(i);
        adapter.setImages(newImages.toArray(new String[newImages.size()]));
    }

    public void submitPost(View view) {
        Intent replyIntent = new Intent();
        Post post = new Post(titleEdit.getText().toString(), contentEdit.getText().toString(),
                newImages.toArray(new String[newImages.size()]),
                UUID.randomUUID().toString().substring(0, 6),
                UUID.randomUUID().toString().substring(0, 4));
        replyIntent.putExtra(NEW_POST, post);

        titleEdit.setText("");
        contentEdit.setText("");
        newImages.clear();

//        SharedPreferences.Editor preferencesEditor = preferences.edit();
//        preferencesEditor.clear();
//        preferencesEditor.apply();

        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(TITLE, titleEdit.getText().toString());
        preferencesEditor.putString(CONTENT, contentEdit.getText().toString());
        preferencesEditor.putInt(IMAGE_COUNT, newImages.size());
        for (int i = 0; i < newImages.size(); i++) {
            preferencesEditor.putString(String.format(INDEXED_IMAGE, i), newImages.get(i).toString());
        }
        preferencesEditor.apply();
    }
}