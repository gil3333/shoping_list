package com.example.myapplication2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FragmentThr extends Fragment {

    private TextView usernameTextView, productListTextView;
    private EditText productNameEditText, productQuantityEditText;
    private Button addProductButton, removeProductButton;

    private String currentUsername;
    private DatabaseReference userShoppingListRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_th, container, false);

        // אתחול רכיבי ה-UI
        usernameTextView = view.findViewById(R.id.usernameTextView);
        productListTextView = view.findViewById(R.id.productListTextView);
        productNameEditText = view.findViewById(R.id.productNameEditText);
        productQuantityEditText = view.findViewById(R.id.productQuantityEditText);
        addProductButton = view.findViewById(R.id.addProductButton);
        removeProductButton = view.findViewById(R.id.removeProductButton);

        // בדיקה אם כל הרכיבים נטענו כראוי
        if (usernameTextView == null || productListTextView == null ||
                productNameEditText == null || productQuantityEditText == null ||
                addProductButton == null || removeProductButton == null) {
            throw new IllegalStateException("שגיאה: אחד מרכיבי ה-UI לא אותחל! בדוק את IDs ב-XML.");
        }

        // בדיקה אם המשתמש מחובר
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(requireContext(), "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return view;
        }



        String currentUsername = "gil222"; // כאן צריך לשים את שם המשתמש המחובר

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);

        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    String username = snapshot.getValue(String.class);
                    usernameTextView.setText(getString(R.string.welcome_message, username));
                } else {
                    usernameTextView.setText("שם משתמש לא נמצא");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "שגיאה בקריאת שם המשתמש", Toast.LENGTH_SHORT).show();
            }
        });


        // אתחול הפניה לרשימת הקניות
        userShoppingListRef = userRef.child("shoppingList");

        // קריאה לעדכון רשימת המוצרים
        updateProductList();

        // **הוספת מאזינים לכפתורים**
        addProductButton.setOnClickListener(v -> {
            String productName = productNameEditText.getText().toString().trim();
            String quantityText = productQuantityEditText.getText().toString().trim();

            if (productName.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(requireContext(), "אנא מלא את שם המוצר והכמות", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    Toast.makeText(requireContext(), "אנא הזן כמות גדולה מ-0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "אנא הזן מספר תקין לכמות", Toast.LENGTH_SHORT).show();
                return;
            }

            userShoppingListRef.child(productName).setValue(quantity).addOnSuccessListener(this::onSuccess).addOnFailureListener(e -> Toast.makeText(requireContext(), "שגיאה בהוספת המוצר", Toast.LENGTH_SHORT).show());
        });

        removeProductButton.setOnClickListener(v -> {
            String productName = productNameEditText.getText().toString().trim();

            if (productName.isEmpty()) {
                Toast.makeText(requireContext(), "אנא הכנס שם מוצר להסרה", Toast.LENGTH_SHORT).show();
                return;
            }

            userShoppingListRef.child(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userShoppingListRef.child(productName).removeValue().addOnSuccessListener(unused -> {
                            Toast.makeText(requireContext(), "המוצר הוסר בהצלחה", Toast.LENGTH_SHORT).show();
                            productNameEditText.setText("");
                            updateProductList();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "שגיאה בהסרת המוצר", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(requireContext(), "המוצר לא נמצא ברשימה", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "שגיאה בבדיקת המוצר: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }




    private void updateProductList() {
        userShoppingListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder productList = new StringBuilder();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productName = productSnapshot.getKey();
                    int quantity = productSnapshot.getValue(Integer.class);
                    productList.append(productName).append(": ").append(quantity).append("\n");
                }
                productListTextView.setText(productList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "שגיאה בעדכון רשימת המוצרים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onSuccess(Void unused) {
        Toast.makeText(requireContext(), "המוצר נוסף בהצלחה", Toast.LENGTH_SHORT).show();
        productNameEditText.setText("");
        productQuantityEditText.setText("");
        updateProductList();
    }
}

