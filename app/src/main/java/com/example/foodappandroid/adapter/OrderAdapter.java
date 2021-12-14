package com.example.foodappandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodappandroid.databinding.ItemOrderSummaryBinding;
import com.example.foodappandroid.model.FoodModel;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<FoodModel> list;


    public OrderAdapter(Context context, List<FoodModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderSummaryBinding binding = ItemOrderSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FoodModel model = list.get(position);
        holder.binding.tvName.setText(model.getName());
        holder.binding.tvQty.setText(" X " + String.valueOf(model.getQuantity()));
        holder.binding.tvTotal.setText("£"+String.valueOf(model.getPrice()));

        Glide.with(context)
                .load(model.getImage())
                .into(holder.binding.ivFood);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private ItemOrderSummaryBinding binding;

        public OrderViewHolder(@NonNull ItemOrderSummaryBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
