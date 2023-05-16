package com.example.myapp.activity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;

public class EditProfileActivity extends AppCompatActivity {
    TextView avatarView;
    TextView usernameView;
    Button editAvatarButton;
    Button editUsernameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        avatarView = findViewById(R.id.avatarView);
        usernameView = findViewById(R.id.usernameView);
        editAvatarButton = findViewById(R.id.editAvatarButton);
        editUsernameButton = findViewById(R.id.editUsernameButton);

        editUsernameButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Username");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newUsername = input.getText().toString();
                // TODO: Save the new username
                usernameView.setText(newUsername);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
        editAvatarButton.setOnClickListener(v -> {
            // TODO: Handle avatar editing
        });

        // TODO: Retrieve and display the current user's avatar and username
    }
}

