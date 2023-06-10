package com.example.myapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.example.myapp.connection.HTTPRequest;
import com.example.myapp.connection.TokenManager;
import com.example.myapp.utils.Base64Encoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EditProfileActivity extends AppCompatActivity {
    ImageView avatarImageView;
    TextView usernameView;
    TextView descriptionView;
    TextView passwordView;
    Button editAvatarButton;
    Button editUsernameButton;
    Button editDescriptionButton;
    Button editPasswordButton;
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    String newAvatar = Base64Encoder.encodeFromUri(getContentResolver(), uri, 100);
                    // HTTP request
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("avatar", newAvatar);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    HTTPRequest.put("account/profile", jsonBody.toString(), TokenManager.getSavedToken(EditProfileActivity.this), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            runOnUiThread(() -> {
                                String avatarMessage = "";
                                if (response.isSuccessful()) {
                                    avatarMessage = "头像修改成功";
                                    byte[] imageBytes = Base64.decode(newAvatar, Base64.DEFAULT);
                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    avatarImageView.setImageBitmap(decodedImage);
                                } else {
                                    avatarMessage = "头像修改失败, 请重试";
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                                builder.setMessage(avatarMessage)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            });
                        }
                    });
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        avatarImageView = findViewById(R.id.avatarImageView);
        usernameView = findViewById(R.id.usernameView);
        descriptionView = findViewById((R.id.descriptionView));
        passwordView = findViewById(R.id.passwordView);
        avatarImageView = findViewById(R.id.avatarImageView);

        editAvatarButton = findViewById(R.id.editAvatarButton);
        editUsernameButton = findViewById(R.id.editUsernameButton);
        editDescriptionButton = findViewById(R.id.editDescriptionButton);
        editPasswordButton = findViewById(R.id.editPasswordButton);

        String username = TokenManager.getSavedUsername(this);
        String token = TokenManager.getSavedToken(this);

        HTTPRequest.get("account/profile/" + username, token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }

                String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String avatarBase64 = jsonObject.getString("avatar");
                    String description = jsonObject.getString("description");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            usernameView.setText(username);
                            descriptionView.setText(description);
                            byte[] imageBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            avatarImageView.setImageBitmap(decodedImage);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        editUsernameButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("编辑用户名");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newUsername = input.getText().toString();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("username", newUsername);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HTTPRequest.put("account/username", jsonBody.toString(), TokenManager.getSavedToken(this), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(() -> {
                            String usernameMessage = "";
                            if (response.isSuccessful()) {
                                usernameMessage = "用户名修改成功";
                                usernameView.setText(newUsername);
                                TokenManager.changeUsername(newUsername, EditProfileActivity.this);
                            } else {
                                usernameMessage = "用户名修改失败, 请重试";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                            builder.setMessage(usernameMessage)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        });
                    }

                });
                usernameView.setText(newUsername);
            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        editAvatarButton.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });

        editDescriptionButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("编辑个人描述");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newDescription = input.getText().toString();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("description", newDescription);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HTTPRequest.put("account/profile", jsonBody.toString(), TokenManager.getSavedToken(this), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(() -> {
                            String descriptionMessage = "";
                            if (response.isSuccessful()) {
                                descriptionMessage = "简介修改成功";
                                descriptionView.setText(newDescription);
                            } else {
                                descriptionMessage = "简介修改失败, 请重试";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                            builder.setMessage(descriptionMessage)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        });
                    }
                });
            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

            builder.show();
        });


        editPasswordButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改密码");

            // 两个输入框，一个用于旧密码，一个用于新密码
            final EditText inputOld = new EditText(this);
            inputOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputOld.setHint("旧密码");
            final EditText inputNew = new EditText(this);
            inputNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputNew.setHint("新密码");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(inputOld);
            layout.addView(inputNew);
            builder.setView(layout);

            builder.setPositiveButton("确定", (dialog, which) -> {
                String oldPassword = inputOld.getText().toString();
                String newPassword = inputNew.getText().toString();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("old_password", oldPassword);
                    jsonBody.put("password", newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HTTPRequest.put("account/password", jsonBody.toString(), TokenManager.getSavedToken(this), new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(() -> {
                            String passwordMessage = "";
                            if (response.isSuccessful()) {
                                passwordMessage = "密码修改成功";
                            } else {
                                passwordMessage = "密码修改失败, 请重试";
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                            builder.setMessage(passwordMessage)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        });
                    }
                });
            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        // TODO: Retrieve and display the current user's avatar and username
    }
}

