package com.example.foodappandroid.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodappandroid.databinding.ActivityPaymentBinding;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.ui.order.OrderSuccessActivity;
import com.example.foodappandroid.util.NetworkManager;

import java.io.Serializable;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private String total;
    private List<FoodModel> foodList;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getFromIntent();
        initView();


    }

    private void getFromIntent() {
        try {
            foodList = (List<FoodModel>) getIntent().getSerializableExtra("orderList");
            total = getIntent().getStringExtra("total");
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.tvCash.setText("Amount to be paid : £"+ total);
    }

    private void initView() {
        getSupportActionBar().hide();
        binding.radioDebit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.cardDebitDetails.setVisibility(View.VISIBLE);
                    binding.buttonConfirmOrder.setVisibility(View.GONE);
                    binding.radioCod.setChecked(false);
                } else {
                    binding.cardDebitDetails.setVisibility(View.GONE);

                }
            }
        });

        binding.radioCod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.radioDebit.setChecked(false);
                    binding.buttonConfirmOrder.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.buttonDebitPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etCard.getText().toString().isEmpty()) {
                    if (!binding.etExpiry.getText().toString().isEmpty()) {
                        if (!binding.etCvv.getText().toString().isEmpty()) {
                            type = "card";
                            if (NetworkManager.isNetworkAvailable(PaymentActivity.this)) {
                                goToOrderSuccess();
                            }else {
                                binding.containerNoInternet.setVisibility(View.VISIBLE);
                            }
                        } else binding.etCvv.setError("Please enter CVV");
                    } else binding.etExpiry.setError("Please enter expiry");
                } else binding.etCard.setError("Please enter card no");
            }
        });

        binding.buttonConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "cod";
                if (NetworkManager.isNetworkAvailable(PaymentActivity.this)) {
                    goToOrderSuccess();
                } else {
                    binding.containerNoInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void goToOrderSuccess() {
        Intent intent = new Intent(this, OrderSuccessActivity.class);
        intent.putExtra("orderList", (Serializable) foodList);
        intent.putExtra("grandTotal", total);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

}