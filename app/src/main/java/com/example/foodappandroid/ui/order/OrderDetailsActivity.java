package com.example.foodappandroid.ui.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.adapter.OrderDetailsAdapter;
import com.example.foodappandroid.adapter.ShopAdapter;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.databinding.ActivityOrderDetailsBinding;
import com.example.foodappandroid.model.OrderItemModel;
import com.example.foodappandroid.model.ShopModel;
import com.example.foodappandroid.ui.home.HomeActivity;
import com.example.foodappandroid.util.NetworkManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderDetailsActivity extends BaseActivity {
    private ActivityOrderDetailsBinding binding;
    private SessionManager sessionManager;
    private final ArrayList<OrderItemModel> orderList = new ArrayList<>();
    private OrderDetailsAdapter ordersAdapter;
    private FirebaseFirestore fb;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setupObserver();
    }

    private void initView() {
        fb = getFireStoreInstance();
        sessionManager = new SessionManager(OrderDetailsActivity.this);
        currentUserId = sessionManager.getUserId();
        getSupportActionBar().hide();
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupObserver();
            }
        });
        setupOrdersRecyclerView();
    }

    private void setupObserver() {
        stopIfRefreshing();
        if (NetworkManager.isNetworkAvailable(OrderDetailsActivity.this)) {
            showShimmer();
            fb.collection("orders")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                orderList.clear();
                                binding.swipeRefreshLayout.setRefreshing(false);
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (documentSnapshot.get("userid").toString().equals(currentUserId)) {
                                        orderList.add(
                                                new OrderItemModel(
                                                        documentSnapshot.get("restaurant").toString(),
                                                        documentSnapshot.get("date").toString(),
                                                        documentSnapshot.get("amount").toString(),
                                                        documentSnapshot.get("items").toString(),
                                                        documentSnapshot.get("type").toString(),
                                                        documentSnapshot.get("userid").toString()
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
                    showToast(OrderDetailsActivity.this, e.getMessage());
                }
            });
        } else {
            stopShimmer();
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.layoutStates.setVisibility(View.GONE);
            showSnackBar(binding.getRoot(), getString(R.string.check_internet));
        }
    }

    private void updateRecyclerView() {
        stopShimmer();
        if (orderList != null && !orderList.isEmpty()) {
            ordersAdapter.notifyDataSetChanged();
            binding.recyclerOrders.setVisibility(View.VISIBLE);
            binding.ivNoOrder.setVisibility(View.GONE);
        } else {
            binding.recyclerOrders.setVisibility(View.GONE);
            binding.ivNoOrder.setVisibility(View.VISIBLE);
            showSnackBar(binding.getRoot(), getString(R.string.no_order));
        }
    }

    private void setupOrdersRecyclerView() {
        ordersAdapter = new OrderDetailsAdapter(this, orderList);
        binding.recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerOrders.setHasFixedSize(true);
        binding.recyclerOrders.setAdapter(ordersAdapter);
    }

    private void showShimmer() {
        binding.layoutStates.setVisibility(View.VISIBLE);
        binding.recyclerOrders.setVisibility(View.GONE);
        binding.ivNoOrder.setVisibility(View.GONE);
        binding.layoutStates.startShimmer();
    }

    private void stopShimmer() {
        binding.layoutStates.setVisibility(View.GONE);
        binding.recyclerOrders.setVisibility(View.VISIBLE);
        binding.layoutStates.stopShimmer();
    }

    private void stopIfRefreshing() {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.layoutStates.setVisibility(View.VISIBLE);
        }
    }
}