package com.example.foodappandroid.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodappandroid.adapter.OrderAdapter;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.databinding.ActivityOrderSummaryBinding;
import com.example.foodappandroid.model.AddressModel;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.ui.address.AddressActivity;
import com.example.foodappandroid.ui.payment.PaymentActivity;
import com.example.foodappandroid.util.NetworkManager;

import java.io.Serializable;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {
    private ActivityOrderSummaryBinding binding;
    private List<FoodModel> foodList;
    private String total;
    private SessionManager manager;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFromIntent();
        initView();
        getAddress();
    }

    private void getAddress() {
        AddressModel model = manager.getAddress();
        StringBuilder builder = new StringBuilder();
        builder.append(
                model.getName() + "\n" +
                        model.getAddress() + "\n" +
                        model.getApartment() + "\n" +
                        model.getCity() + "\n" +
                        model.getPhone() + "\n" +
                        model.getZipcode() + "\n"
        );
        binding.tvAddress.setText(builder.toString());
    }

    private void setFromIntent() {
        try {
            Intent i = getIntent();
            foodList = (List<FoodModel>) i.getSerializableExtra("orderList");
            Log.d("foodlistSummary", foodList.toString());
            total = i.getStringExtra("grandTotal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        manager = new SessionManager(this);
        binding.textTotal.setText("£"+ total);
        binding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderSummaryActivity.this, AddressActivity.class);
                intent.putExtra("edit", "edit");
                startActivity(intent);
            }
        });
        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkManager.isNetworkAvailable(OrderSummaryActivity.this)) {
                    Intent intent = new Intent(OrderSummaryActivity.this, PaymentActivity.class);
                    intent.putExtra("total", total);
                    intent.putExtra("orderList", (Serializable) foodList);
                    startActivity(intent);
                } else {
                    binding.containerNoInternet.setVisibility(View.VISIBLE);
                }
            }
        });
        setupSummaryRecyclerview();
    }

    private void setupSummaryRecyclerview() {
        orderAdapter = new OrderAdapter(this, foodList);
        binding.recyclerFoodItems.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFoodItems.setHasFixedSize(true);
        binding.recyclerFoodItems.setAdapter(orderAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddress();
    }
}