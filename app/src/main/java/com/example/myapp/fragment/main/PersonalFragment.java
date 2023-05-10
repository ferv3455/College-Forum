package com.example.myapp.fragment.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapp.R;
import com.example.myapp.activity.LoginActivity;
import com.example.myapp.activity.SpaceActivity;

public class PersonalFragment extends Fragment {
    Button personalSpaceButton;
    Button loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        personalSpaceButton = view.findViewById(R.id.personalSpaceButton);
        personalSpaceButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SpaceActivity.class);
            startActivity(intent);
        });

        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        return view;
    }
}