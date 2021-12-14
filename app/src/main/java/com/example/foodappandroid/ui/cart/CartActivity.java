package com.example.foodappandroid.ui.cart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.adapter.CartAdapter;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.data.room.CartDatabase;
import com.example.foodappandroid.data.room.entity.Cart;
import com.example.foodappandroid.databinding.ActivityCartBinding;
import com.example.foodappandroid.model.AddressModel;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.ui.address.AddressActivity;
import com.example.foodappandroid.ui.order.OrderSummaryActivity;
import com.example.foodappandroid.util.NetworkManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity implements CartAdapter.onCartItemClickListener {
    private ActivityCartBinding binding;
    private SessionManager sessionManager;
    private ArrayList<FoodModel> foodList = new ArrayList<>();
    private ArrayList<FoodModel> oldFoodList = new ArrayList<>();
    private List<Cart> cartList = new ArrayList<>();
    private CartAdapter cartAdapter;
    private CartDatabase db;
    private float total = 0;
    private String formattedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setupObserver();
        setGrandTotal();
    }

    private void setupObserver() {
        showLoading(this);
        cartList.clear();
        cartList = db.orderDao().getOrderByUserId(sessionManager.getUserId());
        Log.d("cart", cartList.toString());
        for (Cart item : cartList) {
            foodList.add(
                    new FoodModel(
                            item.food.getName(),
                            item.food.getImage(),
                            item.food.getPrice(),
                            item.food.getRestaurant(),
                            item.quantity
                    )
            );
        }
        oldFoodList.addAll(foodList);
        updateRecyclerView();
    }

    private void initView() {
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        db = CartDatabase.getAppDatabase(this);
        setupCartRecyclerView();

        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkManager.isNetworkAvailable(CartActivity.this)) {
                    if (total > 0) {
                        AddressModel model = sessionManager.getAddress();
                        Intent intent;
                        if (model == null) {
                            intent = new Intent(CartActivity.this, AddressActivity.class);
                        } else {
                            intent = new Intent(CartActivity.this, OrderSummaryActivity.class);
                            intent.putExtra("orderList", (Serializable) foodList);
                            intent.putExtra("grandTotal", formattedString);
                        }
                        startActivity(intent);
                /*   Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                   startActivity(intent);*/
                    }
                } else {
                    binding.containerNoInternet.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void setupCartRecyclerView() {
        cartAdapter = new CartAdapter(this, foodList, this);
        binding.recyclerFoodItems.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFoodItems.setHasFixedSize(true);
        binding.recyclerFoodItems.setAdapter(cartAdapter);
    }

    private void updateRecyclerView() {
        hideLoading();
        if (foodList != null && !foodList.isEmpty()) {
            cartAdapter.notifyDataSetChanged();
            binding.layoutShop.textShopName.setText(sessionManager.getCRestaurant());
            binding.recyclerFoodItems.setVisibility(View.VISIBLE);
            binding.layoutShop.getRoot().setVisibility(View.VISIBLE);
            binding.layoutTotal.setVisibility(View.VISIBLE);
            binding.ivNoCart.setVisibility(View.GONE);
            binding.buttonUpdate.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerFoodItems.setVisibility(View.GONE);
            showSnackBar(binding.getRoot(), getString(R.string.no_items));
            binding.recyclerFoodItems.setVisibility(View.GONE);
            binding.layoutShop.getRoot().setVisibility(View.GONE);
            binding.layoutTotal.setVisibility(View.GONE);
            binding.ivNoCart.setVisibility(View.VISIBLE);
            binding.buttonUpdate.setVisibility(View.GONE);

        }
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onQuantityAdd(int position) {
        FoodModel model = foodList.get(position);
        model.setQuantity(model.getQuantity() + 1);
        cartAdapter.notifyDataSetChanged();
        setGrandTotal();
    }

    private void setGrandTotal() {
        total = 0;
        for (int i = 0; i < foodList.size(); i++) {
            for (int j = 1; j <= foodList.get(i).getQuantity(); j++) {
                total += Float.parseFloat(foodList.get(i).getPrice());
            }
        }
        formattedString = String.format("%.02f", total);
        binding.textTotal.setText("£"+String.valueOf(formattedString));
        Log.d("foodList", foodList.toString());
    }

    @Override
    public void onQuantitySub(int position) {
        FoodModel model = foodList.get(position);
        model.setQuantity(model.getQuantity() - 1);
        Log.d("foodList", foodList.toString());
        cartAdapter.notifyDataSetChanged();
        setGrandTotal();
    }

    @Override
    public void onDeleteItem(int position) {
        Cart model = cartList.get(position);
        db.orderDao().deleteCartById(model.orderId);
        showToast(this, "Deleted Successfully");
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        setupObserver();
    }

}