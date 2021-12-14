package com.example.foodappandroid.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.data.room.CartDatabase;
import com.example.foodappandroid.data.room.entity.Cart;
import com.example.foodappandroid.databinding.ActivityOrderSuccessBinding;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.ui.home.HomeActivity;
import com.example.foodappandroid.ui.login.LoginActivity;
import com.example.foodappandroid.ui.signup.SignupActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderSuccessActivity extends BaseActivity {
    private ActivityOrderSuccessBinding binding;
    private String total;
    private String type;
    private List<FoodModel> foodList;
    private FirebaseFirestore fb;
    private CartDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showLoadingLottie();
        getFromIntent();
        initView();
        addDataToFireBase();

    }

    private void clearCartAndSession() {
        database.orderDao().deleteAll();
        sessionManager.setCurrentRestaurant("");
    }

    private void addDataToFireBase() {
        //to store food items
        StringBuilder builder = new StringBuilder();
        for (FoodModel model : foodList) {
            builder.append(
                    model.getQuantity() + " X " + model.getName() + "\n"
            );
        }

        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("restaurant", foodList.get(0).getRestaurant());
        orderItem.put("amount", String.valueOf(total));
        orderItem.put("date", currentDate());
        orderItem.put("items", builder.toString());
        orderItem.put("type", type);
        orderItem.put("userid", sessionManager.getUserId());
        Log.d("orderData", orderItem.toString());

        fb.collection("orders")
                .add(orderItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showSuccessLottie();
                        clearCartAndSession();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(OrderSuccessActivity.this, getString(R.string.error));
            }
        });
    }

    private void getFromIntent() {
        try {
            foodList = (List<FoodModel>) getIntent().getSerializableExtra("orderList");
            total = getIntent().getStringExtra("grandTotal");
            type = getIntent().getStringExtra("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        fb = getFireStoreInstance();
        database = CartDatabase.getAppDatabase(this);
        binding.lottie.setAnimation("loading.json");
        binding.lottie.loop(true);
        binding.lottie.playAnimation();
        binding.btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderSuccessActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showLoadingLottie() {
        binding.lottie.setAnimation("loading.json");
        binding.lottie.loop(true);
        binding.lottie.playAnimation();
        binding.tvSuccess.setVisibility(View.GONE);
        binding.btnSummary.setVisibility(View.GONE);
    }

    private void showSuccessLottie() {
        binding.lottie.setAnimation("order_success.json");
        binding.lottie.loop(false);
        binding.lottie.playAnimation();
        binding.tvSuccess.setVisibility(View.VISIBLE);
        binding.btnSummary.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}