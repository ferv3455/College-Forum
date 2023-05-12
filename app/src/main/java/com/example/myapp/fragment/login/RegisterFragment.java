package com.example.myapp.fragment.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapp.R;
import com.example.myapp.connection.TokenManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterFragment extends Fragment {
    EditText usernameEdit;
    EditText passwordEdit;
    Button registerButton;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        usernameEdit = view.findViewById(R.id.usernameRegisterEdit);
        passwordEdit = view.findViewById(R.id.passwordRegisterEdit);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> TokenManager.registerToken(getActivity(),
                usernameEdit.getText().toString(), passwordEdit.getText().toString(),
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Intent replyIntent = new Intent();
                        getActivity().setResult(Activity.RESULT_OK, replyIntent);
                        getActivity().finish();
                    }
                }));

        return view;
    }
}