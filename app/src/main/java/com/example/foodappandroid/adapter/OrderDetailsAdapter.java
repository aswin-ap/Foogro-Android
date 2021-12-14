package com.example.foodappandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodappandroid.databinding.ItemOrderBinding;
import com.example.foodappandroid.databinding.ItemOrderSummaryBinding;
import com.example.foodappandroid.model.FoodModel;
import com.example.foodappandroid.model.OrderItemModel;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {
    private Context context;
    private List<OrderItemModel> list;


    public OrderDetailsAdapter(Context context, List<OrderItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderDetailsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        OrderItemModel model = list.get(position);
        holder.binding.textShopName.setText(model.getRestaurantName());
        holder.binding.textOrderTime.setText(model.getDate());
        holder.binding.textOrderPrice.setText("£" +model.getAmount());
        holder.binding.textOrderItems.setText(model.getItems());
        holder.binding.tvType.setText("Payment method : " + model.getType());
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
        private ItemOrderBinding binding;

        public OrderDetailsViewHolder(@NonNull ItemOrderBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
