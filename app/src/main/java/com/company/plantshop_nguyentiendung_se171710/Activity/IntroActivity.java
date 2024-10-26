package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.company.plantshop_nguyentiendung_se171710.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends BaseActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        binding.startBtn.setOnClickListener(v -> {
            if (user != null && user.getEmail().contains("admin")) {
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        binding.navigateToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, Login.class));
            finish();
        });
    }
}