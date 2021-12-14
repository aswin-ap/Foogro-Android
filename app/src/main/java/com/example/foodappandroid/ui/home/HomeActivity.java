package com.example.foodappandroid.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foodappandroid.BaseActivity;
import com.example.foodappandroid.R;
import com.example.foodappandroid.adapter.ShopAdapter;
import com.example.foodappandroid.data.preferences.SessionManager;
import com.example.foodappandroid.databinding.ActivityHomeBinding;
import com.example.foodappandroid.model.ShopModel;
import com.example.foodappandroid.ui.cart.CartActivity;
import com.example.foodappandroid.ui.login.LoginActivity;
import com.example.foodappandroid.ui.order.OrderDetailsActivity;
import com.example.foodappandroid.ui.profile.ProfileActivity;
import com.example.foodappandroid.ui.restaurant.RestaurantActivity;
import com.example.foodappandroid.util.LogoutDialog;
import com.example.foodappandroid.util.LogoutListener;
import com.example.foodappandroid.util.NetworkManager;
import com.example.foodappandroid.util.OnItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends BaseActivity implements OnItemClickListener, LogoutListener {
    private ActivityHomeBinding binding;
    private SessionManager sessionManager;
    private final ArrayList<ShopModel> shopList = new ArrayList<>();
    private ShopAdapter shopAdapter;
    private long mLastClickTime = 0;
    private FirebaseFirestore fb;
    private LogoutDialog dialog;
    private LocationManager locationManager;
    private String currentLocation;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestLocationPermission();
        initView();
        setupObserver();

    }

    private void setGreeting() {
        binding.textGreeting.setText(
                "Welcome Back \n" + sessionManager.getUserName()
        );
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull List<Location> locations) {
                }

                @Override
                public void onFlushComplete(int requestCode) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }

                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (!addresses.isEmpty()) {
                            currentLocation = addresses.get(0).getLocality();
                            city = addresses.get(0).getSubAdminArea();
                            setCurrentLocation();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentLocation() {
        binding.tvMainLocation.setText(currentLocation);
        binding.tvSubLocation.setText(city);
    }

    private void setupObserver() {
        showShimmer();
        if (!binding.swipeRefreshLayout.isRefreshing()) {
            binding.layoutStates.setVisibility(View.VISIBLE);
            binding.tvNoInternet.setVisibility(View.GONE);
        }
        if (NetworkManager.isNetworkAvailable(HomeActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            fb.collection("restaurants")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                shopList.clear();
                                binding.swipeRefreshLayout.setRefreshing(false);
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    shopList.add(
                                            new ShopModel(
                                                    documentSnapshot.get("name").toString(),
                                                    documentSnapshot.get("image").toString(),
                                                    documentSnapshot.get("open").toString(),
                                                    documentSnapshot.get("rating").toString()
                                            )
                                    );
                                }
                                updateRecyclerView();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    stopShimmer();
                    binding.swipeRefreshLayout.setRefreshing(false);
                    showToast(HomeActivity.this, e.getMessage());
                }
            });
        } else {
            stopShimmer();
            binding.recyclerShops.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.tvNoInternet.setVisibility(View.VISIBLE);

        }
    }

    private void updateRecyclerView() {
        stopShimmer();
        if (shopList != null && !shopList.isEmpty()) {
            shopAdapter.notifyDataSetChanged();
        } else {
            binding.recyclerShops.setVisibility(View.GONE);
            showSnackBar(binding.getRoot(), getString(R.string.no_items));
        }
    }

    private void showShimmer() {
        binding.layoutStates.setVisibility(View.VISIBLE);
        binding.recyclerShops.setVisibility(View.GONE);
        binding.tvNoInternet.setVisibility(View.GONE);
        binding.layoutStates.startShimmer();
    }

    private void stopShimmer() {
        binding.layoutStates.setVisibility(View.GONE);
        binding.recyclerShops.setVisibility(View.VISIBLE);
        binding.layoutStates.stopShimmer();
    }

    private void initView() {
        fb = getFireStoreInstance();
        sessionManager = new SessionManager(HomeActivity.this);
        dialog = new LogoutDialog(this, this);
        getSupportActionBar().hide();
        setGreeting();
/*        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);*/
        binding.imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupObserver();
            }
        });
        binding.ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.order_item: {
                        Intent intent = new Intent(HomeActivity.this, OrderDetailsActivity.class);
                        binding.drawerLayout.closeDrawer(Gravity.LEFT);
                        startActivity(intent);
                        break;
                    }

                    case R.id.profile_item: {
                        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        binding.drawerLayout.closeDrawer(Gravity.LEFT);
                        startActivity(intent);
                        break;
                    }

                    case R.id.signout_item: {
                        binding.drawerLayout.closeDrawer(Gravity.LEFT);
                        clearSelection();
                        dialog.show();
                        break;
                    }
                }
                return true;
            }
        });
        setupRestaurantsRecyclerView();
    }

    private void setupRestaurantsRecyclerView() {
        shopAdapter = new ShopAdapter(this, shopList, this);
        binding.recyclerShops.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerShops.setHasFixedSize(true);
        binding.recyclerShops.setAdapter(shopAdapter);
    }

    @Override
    public void onItemClick(Integer position) {
        if (!shopList.isEmpty()) {
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("image", shopList.get(position).getImage());
            intent.putExtra("name", shopList.get(position).getName());
            intent.putExtra("rating", shopList.get(position).getRating());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearSelection();
        setGreeting();
    }

    private void clearSelection() {
        if (binding.navView.getCheckedItem() != null) {
            binding.navView.getCheckedItem().setChecked(false);
        }
    }

    @Override
    public void logOut() {
        sessionManager.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    showToast(HomeActivity.this, "Permission to Location is denied");
                }
                return;
            }
        }
    }
}