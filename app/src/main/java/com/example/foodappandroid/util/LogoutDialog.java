package com.example.foodappandroid.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.foodappandroid.R;
import com.example.foodappandroid.ui.home.HomeActivity;
import com.example.foodappandroid.ui.login.LoginActivity;
import com.google.android.material.button.MaterialButton;

public class LogoutDialog extends Dialog {
    private Context context;
    private LogoutListener logoutListener;

    public LogoutDialog(@NonNull Context context, LogoutListener logoutListener) {
        super(context);
        this.context = context;
        this.logoutListener = logoutListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.layout_log_out);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MaterialButton btnCancel = findViewById(R.id.btn_no);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        MaterialButton btnYes = findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                logoutListener.logOut();
            }
        });

    }


}
