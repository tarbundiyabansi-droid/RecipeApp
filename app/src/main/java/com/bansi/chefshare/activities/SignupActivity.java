package com.bansi.chefshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipeapp.databinding.ActivitySignupBinding;
import com.bansi.chefshare.viewmodels.AuthViewModel;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnSignup.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSignup.setEnabled(false);
                authViewModel.signup(name, email, phone, password);
            }
        });

        binding.tvLoginLink.setOnClickListener(v -> {
            finish();
        });

        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                binding.progressBar.setVisibility(View.GONE);
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finishAffinity();
            }
        });

        authViewModel.getAuthErrorLiveData().observe(this, error -> {
            if (error != null) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSignup.setEnabled(true);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
