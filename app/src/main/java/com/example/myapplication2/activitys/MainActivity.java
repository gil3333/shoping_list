package com.example.myapplication2.activitys;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.myapplication2.R;
import com.example.myapplication2.Users;
import com.example.myapplication2.fragments.FragmentTwo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public FirebaseAuth mAuth; // ניהול אימות משתמשים

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // חיבור ל-Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
    }

    // פונקציה לבדיקת רישום משתמשים
    public void registerUser(String username, String password, String confirmPassword, String phone) {
        // בדיקות תקינות בסיסיות
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "הסיסמאות אינן תואמות", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "הסיסמה חייבת להיות לפחות 6 תווים", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקת ייחודיות שם המשתמש ושמירת המשתמש
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(this, "שם המשתמש כבר תפוס", Toast.LENGTH_SHORT).show();
                } else {
                    // שמירת המשתמש בבסיס הנתונים
                    saveUser(username, password, phone);
                }
            } else {
                Toast.makeText(this, "שגיאה בחיבור לשרת", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציה לשמירת נתוני משתמש בבסיס הנתונים
    private void saveUser(String username, String password, String phone) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("username", username); // שם המשתמש
            userMap.put("password", password); // מומלץ להשתמש ב-hashing לסיסמה
            userMap.put("phone", phone);
            userMap.put("shoppingList", new HashMap<>()); // רשימה ריקה

            databaseReference.setValue(userMap)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "הרישום הצליח!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "הרישום נכשל: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


    private void addProductToShoppingList(String username, String productName, int quantity) {
        DatabaseReference userShoppingListRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("shoppingList");

        userShoppingListRef.child(productName).setValue(quantity)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "המוצר נוסף בהצלחה!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהוספת המוצר: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void removeProductFromShoppingList(String username, String productName) {
        DatabaseReference userShoppingListRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(username)
                .child("shoppingList");

        userShoppingListRef.child(productName).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "המוצר הוסר בהצלחה!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהסרת המוצר: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void login(String username, String password, View view) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(this, "אנא מלא את שם המשתמש והסיסמה", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // בדיקה אם שם המשתמש קיים
        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // בדיקת סיסמה
                    String storedPassword = snapshot.child("password").getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(password)) {
                        // שמירת שם המשתמש ב-SharedPreferences
                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("username", username)
                                .apply();

                        Toast.makeText(MainActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();

                        // ניווט לפרגמנט 3
                        Navigation.findNavController(view).navigate(R.id.action_fragmentOne_to_fragmentTh);
                    } else {
                        Toast.makeText(MainActivity.this, "סיסמה שגויה", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "שם המשתמש לא נמצא", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "שגיאה בקריאת הנתונים: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerWithUsernameKey(String username, String password, String phone) {
        if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // בדיקה אם שם המשתמש קיים
        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, "שם המשתמש כבר תפוס", Toast.LENGTH_SHORT).show();
                } else {
                    // שמירת הנתונים תחת שם המשתמש
                    Users user = new Users(username, password, phone);
                    usersRef.child(username).setValue(user)
                            .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "הרישום בוצע בהצלחה!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "שגיאה ברישום: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "שגיאה: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }







    public void getData(String uid) {
    }
}
