package com.example.searchmovieapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.searchmovieapp.Domain.Film;
import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ArrayList<Film> saved = new ArrayList<>();
    private ArrayList<Film> liked = new ArrayList<>();
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initView();
        loadLoginDetails();
    }

    private void initView() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.login.getText().toString();
                String password = binding.password.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Необходимо ввести логин и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    signIn(email, password);
                }
            }
        });

        binding.newAccountRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.login.getText().toString();
                String password = binding.password.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Необходимо ввести логин и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    register(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveLoginDetails(email, password);

                            updateUI(user);
                        } else {
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Аутентификация не удалась.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Register", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid(); // Получение UID нового пользователя

                            // Создание ссылки на узел пользователя в базе данных
                            DatabaseReference userRef = database.getReference().child("users").child(userId);

                            // Сохранение логина и пароля в базе данных
                            userRef.child("email").setValue(email);
                            userRef.child("password").setValue(password);
                            userRef.child("saved").setValue(saved);
                            userRef.child("saved").setValue(liked);
                            userRef.child("admin").setValue(false);
                            saveLoginDetails(email, password);



                            updateUI(user);
                        } else {
                            Log.w("Register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Регистрация не удалась.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, Main2Activity.class));


            finish();
        }
    }

    private void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private void loadLoginDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String password = sharedPreferences.getString(KEY_PASSWORD, "");
        binding.login.setText(email);
        binding.password.setText(password);
    }
}
