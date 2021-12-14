package com.example.foodappandroid.ui.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.databinding.ActivitySignupBinding;
import com.example.foodappandroid.ui.home.HomeActivity;
import com.example.foodappandroid.util.NetworkManager;
import com.example.foodappandroid.util.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SignupActivity extends BaseActivity {

    private ActivitySignupBinding binding;
    private long mLastClickTime = 0;
    private String uuId;
    private SessionManager sessionManager;
    private String hi = "hi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setListener();

    }

    private void initView() {
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
    }

    private void setListener() {
        binding.ivEyePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
                    binding.editPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    binding.ivEyePassword.setImageDrawable(getDrawable(R.drawable.ic_eye_visibility_off));
                } else {
                    binding.editPassword.setTransformationMethod(new PasswordTransformationMethod());
                    binding.ivEyePassword.setImageDrawable(getDrawable(R.drawable.ic_eye_visible));
                }
                binding.editPassword.setSelection(binding.editPassword.getText().length());
            }
        });

        binding.ivEyeConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editConfirmPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
                    binding.editConfirmPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    binding.ivEyeConfirmPass.setImageDrawable(getDrawable(R.drawable.ic_eye_visibility_off));
                } else {
                    binding.editConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    binding.ivEyeConfirmPass.setImageDrawable(getDrawable(R.drawable.ic_eye_visible));
                }
                binding.editConfirmPassword.setSelection(binding.editConfirmPassword.getText().length());
            }
        });


        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (binding.editName.getText().toString() != null && !binding.editName.getText().toString().isEmpty()) {
                    if (Validation.isValidEmail(Objects.requireNonNull(binding.editEmail.getText()).toString())) {
                        if (binding.editMobile.getText().toString() != null && !binding.editMobile.getText().toString().isEmpty()) {
                            if (Validation.isValidPassword(Objects.requireNonNull(binding.editPassword.getText()).toString())) {
                                if (Validation.isPasswordConfirm(Objects.requireNonNull(binding.editPassword.getText()).toString(),
                                        Objects.requireNonNull(binding.editConfirmPassword.getText()).toString())) {

                                    registerUser();
                                } else
                                    showToast(SignupActivity.this, "Password confirmation failed");
                            } else binding.editPassword.setError("Please enter valid password");
                        } else binding.editMobile.setError("Please enter mobile number");
                    } else binding.editEmail.setError("Please enter valid email");
                } else binding.editName.setError("Please enter valid name");
            }
        });
    }

    private void registerUser() {
        if (NetworkManager.isNetworkAvailable(SignupActivity.this)) {
            showLoading(this);

            Map<String, Object> user = new HashMap<>();
            user.put("name", binding.editName.getText().toString());
            user.put("email", binding.editEmail.getText().toString());
            user.put("password", binding.editPassword.getText().toString());
            user.put("mobile", binding.editMobile.getText().toString());
            uuId = UUID.randomUUID().toString();
            user.put("userid", uuId);

            FirebaseFirestore fireStoreInstance = getFireStoreInstance();
            fireStoreInstance.collection("user")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            hideLoading();
                            sessionManager.setUserId(uuId);
                            sessionManager.setLogin(true);
                            sessionManager.setUserName(binding.editName.getText().toString());
                            sessionManager.setDocumentId(documentReference.getId());
                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            showToast(SignupActivity.this, "Registered Successfully");
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast(SignupActivity.this, getString(R.string.error));
                }
            });
        } else {
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        }
    }


    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignupActivity.this);
        builder.setTitle("Cancel process?");
        builder.setMessage("Are you sure want to cancel the registration process?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SignupActivity.this.finish();
                dialogInterface.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
}