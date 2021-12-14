package com.example.foodappandroid.ui.nointernet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.foodappandroid.R;
import com.example.foodappandroid.databinding.FragmentNoInternetBinding;
import com.example.foodappandroid.util.NetworkManager;
import com.google.android.material.snackbar.Snackbar;

public class NoInternetFragment extends Fragment {
    private FragmentNoInternetBinding binding;
    Snackbar sb;

    public NoInternetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoInternetBinding.inflate(getLayoutInflater());
        binding.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkManager.isNetworkAvailable(getContext())) {
                        binding.mainView.setVisibility(View.GONE);
                } else {
                    sb = Snackbar.make(view,getContext().getString(R.string.check_internet),Snackbar.LENGTH_SHORT);
                    sb.show();
                }
            }

        });
        return binding.getRoot();
    }
}