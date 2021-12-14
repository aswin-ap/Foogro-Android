package com.example.foodappandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodappandroid.databinding.ItemFoodBinding;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.util.OnItemClickListener;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private OnItemClickListener listener;
    private List<FoodModel> list;


    public FoodAdapter(Context context, List<FoodModel> list, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodModel model = list.get(position);
        holder.binding.textFoodName.setText(model.getName());
        holder.binding.textFoodPrice.setText("£ : " + model.getPrice());
        Glide.with(context)
                .load(model.getImage())
                .centerInside()
                .into(holder.binding.imageFood);

        holder.binding.tvAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private ItemFoodBinding binding;

        public FoodViewHolder(@NonNull ItemFoodBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
