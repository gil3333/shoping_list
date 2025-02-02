package com.example.myapplication2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.example.myapplication2.activitys.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOne extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentOne() {
        // Constructor ריק נדרש
    }

    public static FragmentOne newInstance(String param1, String param2) {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // טעינת הפריסה
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        // טיפול בכפתור התחברות
        Button loginButton = view.findViewById(R.id.Login); // ודא שה-ID "Login" קיים ב-XML
        loginButton.setOnClickListener(v -> {
            EditText usernameEditText = view.findViewById(R.id.username);
            EditText passwordEditText = view.findViewById(R.id.password);

            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.login(username, password,view);
            }
        });


        // טיפול בכפתור הרשמה
        Button registerButton = view.findViewById(R.id.Register); // ודא שה-ID "Register" קיים ב-XML
        registerButton.setOnClickListener(v -> {
            // ניווט ל-FragmentTwo
            Navigation.findNavController(view).navigate(R.id.action_fragmentOne_to_fragmentTwo);
        });

        return view;
    }
}


















