package com.example.myapp.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.adapter.EditableGridViewAdapter;
import com.example.myapp.connection.ContentManager;
import com.example.myapp.data.Post;
import com.example.myapp.utils.Base64Encoder;
import com.example.myapp.utils.LocationFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import jp.wasabeef.richeditor.RichEditor;
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
    private RichEditor contentEdit;
    private GridView imagesView;

    private ImageView locationImage;
    private TextView locationText;
    private String location = null;
    private LocationFinder locationFinder;

    private ImageView tagImage;
    private TextView tagEdit;

    private Button submitButton;
    private EditableGridViewAdapter adapter;
    private PopupMenu menu = null;

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

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImageUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                File file = new File(picturePath);
                Log.d("file", file.getAbsolutePath());
                
                if (file.exists()) {
                    Log.d("file", "exist");
                }

                ContentManager.uploadMedia(this, file, new Callback() {
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

        locationImage = findViewById(R.id.locationImageView);
        locationText = findViewById(R.id.locationTextView);
        locationFinder = new LocationFinder(this);
        findViewById(R.id.newLocationLayout).setOnClickListener(v -> {
            if (location == null) {
                if (locationFinder.isValid()) {
                    location = locationFinder.getLocationMsg();
                    locationText.setText(location);
                    setLocationEnabled(true);
                }
                else {
                    locationText.setText("请稍后重试！");
                }
            }
            else {
                location = null;
                setLocationEnabled(false);
            }
        });

        tagImage = findViewById(R.id.tagImageView);
        tagEdit = findViewById(R.id.tagEditView);
        tagEdit.addTextChangedListener(new TextWatcherWithContext(this));

        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        titleEdit.setText(preferences.getString(TITLE, ""));
        contentEdit.setHtml(preferences.getString(CONTENT, ""));
        int imageCount = preferences.getInt(IMAGE_COUNT, 0);
        for (int i = 0; i < imageCount; i++) {
            newImagesId.add(preferences.getString(String.format(INDEXED_ID, i), "ERROR"));
            newImagesThumbnail.add(preferences.getString(String.format(INDEXED_IMAGE, i), "ERROR"));
        }

        adapter = new EditableGridViewAdapter(this, newImagesThumbnail.toArray(new String[newImagesThumbnail.size()]));
        imagesView.setAdapter(adapter);

        // Editor settings
        contentEdit.setEditorHeight(200);
        contentEdit.setEditorFontSize(20);
        contentEdit.setPlaceholder("说些什么吧...");
        contentEdit.setPadding(10, 10, 10, 10);

        // Bind buttons
        bindEditButtons();
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
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
            obj.put("content", contentEdit.getHtml());
            JSONArray imageArray = new JSONArray(newImagesId.toArray(new String[newImagesId.size()]));
            obj.put("images", imageArray);
            JSONArray tagArray = new JSONArray(tagEdit.getText().toString().split(" "));
            obj.put("tags", tagArray);
            if (location != null) {
                obj.put("location", location);
            }
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
                        contentEdit.setHtml("");
                        newImagesThumbnail.clear();
                        Intent replyIntent = new Intent();
                        setResult(RESULT_OK, replyIntent);
                        finish();
                    });
                }
            }
        });
    }

    public void setLocationEnabled(boolean state) {
        if (state) {
            locationImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            locationText.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
        }
        else {
            locationImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
            locationText.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        }
    }

    public void setTagsEnabled(boolean state) {
        if (state) {
            tagImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
            tagEdit.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.purple_500)));
        }
        else {
            tagImage.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
            tagEdit.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(TITLE, titleEdit.getText().toString());
        preferencesEditor.putString(CONTENT, contentEdit.getHtml());
        preferencesEditor.putInt(IMAGE_COUNT, newImagesThumbnail.size());
        for (int i = 0; i < newImagesThumbnail.size(); i++) {
            preferencesEditor.putString(String.format(INDEXED_ID, i), newImagesId.get(i));
            preferencesEditor.putString(String.format(INDEXED_IMAGE, i), newImagesThumbnail.get(i));
        }
        preferencesEditor.apply();

        if (menu != null) {
            menu.dismiss();
        }
    }

    class TextWatcherWithContext implements TextWatcher {
        Context context;

        TextWatcherWithContext(Context context) {
            this.context = context;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            ((NewPostActivity) context).setTagsEnabled(charSequence.length() != 0);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private void bindEditButtons() {
        findViewById(R.id.action_undo).setOnClickListener(v -> contentEdit.undo());
        findViewById(R.id.action_redo).setOnClickListener(v -> contentEdit.redo());
        findViewById(R.id.action_bold).setOnClickListener(v -> contentEdit.setBold());
        findViewById(R.id.action_italic).setOnClickListener(v -> contentEdit.setItalic());
        findViewById(R.id.action_underline).setOnClickListener(v -> contentEdit.setUnderline());
        findViewById(R.id.action_strikethrough).setOnClickListener(v -> contentEdit.setStrikeThrough());
        findViewById(R.id.action_subscript).setOnClickListener(v -> contentEdit.setSubscript());
        findViewById(R.id.action_superscript).setOnClickListener(v -> contentEdit.setSuperscript());
        findViewById(R.id.action_heading1).setOnClickListener(v -> contentEdit.setHeading(1));
        findViewById(R.id.action_heading2).setOnClickListener(v -> contentEdit.setHeading(2));
        findViewById(R.id.action_heading3).setOnClickListener(v -> contentEdit.setHeading(3));
        findViewById(R.id.action_heading4).setOnClickListener(v -> contentEdit.setHeading(4));
        findViewById(R.id.action_heading5).setOnClickListener(v -> contentEdit.setHeading(5));
        findViewById(R.id.action_heading6).setOnClickListener(v -> contentEdit.setHeading(6));
        findViewById(R.id.action_align_left).setOnClickListener(v -> contentEdit.setAlignLeft());
        findViewById(R.id.action_align_center).setOnClickListener(v -> contentEdit.setAlignCenter());
        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> contentEdit.setBullets());
        findViewById(R.id.action_txt_color).setOnClickListener(view -> {
            if (menu != null) {
                menu.dismiss();
            }
            menu = new PopupMenu(this, view);
            menu.getMenuInflater().inflate(R.menu.color_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(txtColorListener);
            menu.show();
        });
    }

    private PopupMenu.OnMenuItemClickListener txtColorListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.black:
                    contentEdit.setTextColor(Color.BLACK);
                    break;
                case R.id.red:
                    contentEdit.setTextColor(Color.RED);
                    break;
                case R.id.green:
                    contentEdit.setTextColor(Color.GREEN);
                    break;
                case R.id.blue:
                    contentEdit.setTextColor(Color.BLUE);
                    break;
                case R.id.cyan:
                    contentEdit.setTextColor(Color.CYAN);
                    break;
                case R.id.magenta:
                    contentEdit.setTextColor(Color.MAGENTA);
                    break;
                case R.id.yellow:
                    contentEdit.setTextColor(Color.YELLOW);
                    break;
                case R.id.white:
                    contentEdit.setTextColor(Color.WHITE);
                    break;
                default:
                    menu.dismiss();
                    menu = null;
                    return false;
            }
            menu.dismiss();
            menu = null;
            return true;
        }
    };
}