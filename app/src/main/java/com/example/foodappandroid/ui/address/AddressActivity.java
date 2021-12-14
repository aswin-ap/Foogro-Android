package com.example.foodappandroid.ui.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.databinding.ActivityAddressBinding;
import com.example.foodappandroid.model.AddressModel;
import com.example.foodappandroid.ui.order.OrderSummaryActivity;

public class AddressActivity extends AppCompatActivity {
    private ActivityAddressBinding binding;
    private SessionManager sessionManager;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFromIntent();
        initView();
    }

    private void setFromIntent() {
        try {
            from = getIntent().getExtras().getString("edit");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        if (from != null) {
            AddressModel model = sessionManager.getAddress();
            binding.editName.setText(model.getName());
            binding.etAddress.setText(model.getAddress());
            binding.etApartment.setText(model.getApartment());
            binding.etCity.setText(model.getCity());
            binding.etPhone.setText(model.getPhone());
            binding.etZipcode.setText(model.getZipcode());
            binding.buttonUpdate.setText("Save Address");
        }

        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.editName.getText().toString().isEmpty()) {
                    if (!binding.etAddress.getText().toString().isEmpty()) {
                        if (!binding.etApartment.getText().toString().isEmpty()) {
                            if (!binding.etCity.getText().toString().isEmpty()) {
                                if (!binding.etPhone.getText().toString().isEmpty()) {
                                    if (!binding.etZipcode.getText().toString().isEmpty()) {
                                        saveAndIntent();
                                    } else
                                        binding.etZipcode.setError("Please enter zip/postal code");
                                } else binding.etZipcode.setError("Please enter Phone no");
                            } else binding.etCity.setError("Please enter city");
                        } else binding.etApartment.setError("Please enter apartment details");
                    } else binding.etAddress.setError("Please enter Address");
                } else binding.editName.setError("Please enter name");
            }
        });
    }

    private void saveAndIntent() {
        AddressModel model = new AddressModel(
                binding.editName.getText().toString(),
                binding.etAddress.getText().toString(),
                binding.etApartment.getText().toString(),
                binding.etCity.getText().toString(),
                binding.etPhone.getText().toString(),
                binding.etZipcode.getText().toString()
        );
        sessionManager.saveAddress(model);
        onBackPressed();
        this.finish();
    }
}