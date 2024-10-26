package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.company.plantshop_nguyentiendung_se171710.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button btnLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    TextView continueAsGuest;
    private FirebaseFirestore firestore;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

//        if (currentUser != null && !currentUser.getEmail().contains("admin")) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (currentUser != null && currentUser.getEmail().contains("admin")) {
            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);
        continueAsGuest = findViewById(R.id.continueAsGuest);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        continueAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

//                mAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressBar.setVisibility(View.GONE);
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(Login.this, "Login successfully.",
//                                            Toast.LENGTH_SHORT).show();
//
//                                    if (email.contains("admin")) {
//                                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                        return;
//                                    }
//
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Toast.makeText(Login.this, "Login failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    firestore.collection("users").document(userId)
                                            .get()
                                            .addOnSuccessListener(document -> {
                                                if (document.exists()) {
                                                    String role = document.getString("role");
                                                    switch (role) {
                                                        case "admin":
                                                            startActivity(new Intent(Login.this, AdminActivity.class));
                                                            finish();
                                                            break;
                                                        case "user":
                                                            startActivity(new Intent(Login.this, MainActivity.class));
                                                            finish();
                                                            break;
                                                        default:
                                                            Log.d("TAG", "User role not defined");
                                                            break;
                                                    }
                                                } else {
                                                    Log.d("TAG", "No such document");
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("TAG", "Error getting user role", e);
                                            });
                                }
                                Toast.makeText(Login.this, "Login successful.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Login.this, "Invalid Email or Password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}