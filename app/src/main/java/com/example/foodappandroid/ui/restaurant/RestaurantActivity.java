package com.example.foodappandroid.ui.restaurant;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.adapter.FoodAdapter;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.data.room.CartDatabase;
import com.example.foodappandroid.data.room.entity.Cart;
import com.example.foodappandroid.databinding.ActivityRestaurantBinding;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.util.ClearCartDialog;
import com.example.foodappandroid.util.ClearCartListener;
import com.example.foodappandroid.util.NetworkManager;
import com.example.foodappandroid.util.OnItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RestaurantActivity extends BaseActivity implements OnItemClickListener, ClearCartListener {
    private ActivityRestaurantBinding binding;
    private String restaurantName;
    private String imageUrl;
    private SessionManager sessionManager;
    private String rating;
    private ArrayList<FoodModel> foodList = new ArrayList<>();
    private FoodAdapter foodAdapter;
    private long mLastClickTime = 0;
    private FirebaseFirestore fb;
    private CartDatabase db;
    private ClearCartDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFromIntent();
        initView();
        setupObserver();
    }

    private void initView() {
        fb = getFireStoreInstance();
        sessionManager = new SessionManager(this);
        db = CartDatabase.getAppDatabase(this);
        dialog = new ClearCartDialog(this, this, "Are you sure to remove " + sessionManager.getCRestaurant() + " foods and clear cart ?");
        getSupportActionBar().hide();
        binding.textShopRating.setText("Rating " + rating);
        binding.toolbarLayout.setTitle(restaurantName);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupObserver();
            }
        });
        setupFoodRecyclerView();
    }

    private void setupObserver() {
        showShimmer();
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            binding.layoutStates.setVisibility(View.VISIBLE);
        }
        if (NetworkManager.isNetworkAvailable(RestaurantActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fb.collection("menu")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                foodList.clear();
                                binding.swipeRefreshLayout.setRefreshing(false);
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (documentSnapshot.get("restaurant").toString().equals(restaurantName)) {
                                        foodList.add(
                                                new FoodModel(
                                                        documentSnapshot.get("name").toString(),
                                                        documentSnapshot.get("image").toString(),
                                                        documentSnapshot.get("price").toString(),
                                                        documentSnapshot.get("restaurant").toString()
                                                )
                                        );
                                    }
                                }
                                updateRecyclerView();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    stopShimmer();
                    binding.swipeRefreshLayout.setRefreshing(false);
                    showToast(RestaurantActivity.this, e.getMessage());
                }
            });
        } else {
            stopShimmer();
            showSnackBar(binding.getRoot(), getString(R.string.check_internet));
        }
    }

    private void updateRecyclerView() {
        stopShimmer();
        if (foodList != null && !foodList.isEmpty()) {
            foodAdapter.notifyDataSetChanged();
        } else {
            binding.recyclerFoodItems.setVisibility(View.GONE);
            showSnackBar(binding.getRoot(), getString(R.string.no_items));
        }
    }

    private void showShimmer() {
        binding.layoutStates.setVisibility(View.VISIBLE);
        binding.recyclerFoodItems.setVisibility(View.GONE);
        binding.layoutStates.startShimmer();
    }

    private void stopShimmer() {
        binding.layoutStates.setVisibility(View.GONE);
        binding.recyclerFoodItems.setVisibility(View.VISIBLE);
        binding.layoutStates.stopShimmer();
    }

    private void setupFoodRecyclerView() {
        foodAdapter = new FoodAdapter(this, foodList, this);
        binding.recyclerFoodItems.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFoodItems.setHasFixedSize(true);
        binding.recyclerFoodItems.setAdapter(foodAdapter);
    }

    private void setFromIntent() {
        try {
            restaurantName = getIntent().getExtras().get("name").toString();
            imageUrl = getIntent().getExtras().get("image").toString();
            rating = getIntent().getExtras().get("rating").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(Integer position) {
        try {
            FoodModel model = foodList.get(position);
            Cart cart = new Cart();
            cart.food = model;
            cart.userId = sessionManager.getUserId();
            cart.status = 0;
            cart.quantity = 1;
            if (!db.orderDao().isFoodIsExist(sessionManager.getUserId(), model)) {
                if (sessionManager.getCRestaurant() == null || sessionManager.getCRestaurant().isEmpty()) {
                    sessionManager.setCurrentRestaurant(model.getRestaurant());
                    db.orderDao().insert(cart);
                    showSnackBar(binding.getRoot(), "Added to cart successfully");
                } else {
                    if (sessionManager.getCRestaurant().equals(model.getRestaurant())) {
                        db.orderDao().insert(cart);
                        showSnackBar(binding.getRoot(), "Added to cart successfully");
                    } else {
                        dialog.show();
                    }
                }
            } else {
                showSnackBar(binding.getRoot(), "Food already in the cart");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearCart() {
        db.orderDao().deleteAll();
        sessionManager.setCurrentRestaurant("");
        showToast(this, "Cart cleared");
    }
}