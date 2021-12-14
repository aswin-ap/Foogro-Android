package com.example.foodappandroid.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.databinding.ActivityLoginBinding;
import com.example.foodappandroid.ui.home.HomeActivity;
import com.example.foodappandroid.ui.nointernet.NoInternetFragment;
import com.example.foodappandroid.ui.signup.SignupActivity;
import com.example.foodappandroid.util.NetworkManager;
import com.example.foodappandroid.util.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private long mLastClickTime = 0;
    private final FirebaseFirestore fb = getFireStoreInstance();
    private boolean isMatch;
    private SessionManager sessionManager;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(LoginActivity.this);
        initView();

    }

    private void initView() {
        getSupportActionBar().hide();
        binding.ivEyePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
                    binding.etPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    binding.ivEyePass.setImageDrawable(getDrawable(R.drawable.ic_eye_visibility_off));
                } else {
                    binding.etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    binding.ivEyePass.setImageDrawable(getDrawable(R.drawable.ic_eye_visible));
                }
                binding.etPassword.setSelection(binding.etPassword.getText().length());
            }
        });

        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.isValidEmail(binding.etUsername.getText().toString())) {
                    if (!binding.etPassword.getText().toString().isEmpty()) {
                        login(binding.etUsername.getText().toString(), binding.etPassword.getText().toString());
                    } else
                        binding.etPassword.setError("Please enter valid password");
                } else
                    binding.etUsername.setError("Please enter valid email");
            }
        });

    }


    private void login(String userName, String passWord) {
        if (NetworkManager.isNetworkAvailable(LoginActivity.this)) {
            binding.containerNoInternet.setVisibility(View.GONE);
            showLoading(this);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fb.collection("user")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                hideLoading();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (documentSnapshot.get("email").toString().equals(userName) &&
                                            documentSnapshot.get("password").toString().equals(passWord)) {

                                        isMatch = true;
                                        sessionManager.setUserId(documentSnapshot.get("userid").toString());
                                        sessionManager.setDocumentId(documentSnapshot.getId());
                                        sessionManager.setUserName(documentSnapshot.get("name").toString());
                                        sessionManager.setLogin(true);
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        showToast(LoginActivity.this, "Login Successfully");
                                        startActivity(intent);
                                        finish();
                                    } else
                                        isMatch = false;
                                }

                                if (!isMatch)
                                    showToast(LoginActivity.this, "Enter valid user details");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast(LoginActivity.this, getString(R.string.error));
                }
            });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
           // showSnackBar(binding.getRoot(), getString(R.string.check_internet));
    }

}