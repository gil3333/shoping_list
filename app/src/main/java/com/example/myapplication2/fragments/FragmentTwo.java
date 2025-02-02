package com.example.myapplication2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.example.myapplication2.activitys.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FragmentTwo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // חיבור בין ה-XML לקוד Java
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        // קישור לשדות הקלט והכפתור
        EditText usernameEditText = view.findViewById(R.id.username1);
        EditText passwordEditText = view.findViewById(R.id.password1);
        EditText confirmPasswordEditText = view.findViewById(R.id.repassword);
        EditText phoneEditText = view.findViewById(R.id.phone);
        Button registerButton = view.findViewById(R.id.registerButton1);

        // כפתור רישום
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // קריאה לפונקציית הרישום שנמצאת ב-MainActivity
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.registerUser(username, password, confirmPassword, phone);
            }
        });

        return view;
    }
}
