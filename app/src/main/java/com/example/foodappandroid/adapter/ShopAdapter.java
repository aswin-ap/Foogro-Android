package com.example.foodappandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodappandroid.databinding.ItemShopBinding;
import com.example.foodappandroid.model.ShopModel;
import com.example.foodappandroid.util.OnItemClickListener;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private Context context;
    private OnItemClickListener listener;
    private List<ShopModel> list;


    public ShopAdapter(Context context, List<ShopModel> list, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public ShopAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShopBinding binding = ItemShopBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ShopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ShopViewHolder holder, int position) {
        ShopModel model = list.get(holder.getAdapterPosition());
        holder.binding.textShopName.setText(model.getName());
        holder.binding.textShopDesc.setText("Opens at "+model.getOpen());
        holder.binding.textShopRating.setText(model.getRating());
        Glide.with(context)
                .load(model.Image)
                .circleCrop()
                .into(holder.binding.imageShop);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        private ItemShopBinding binding;

        public ShopViewHolder(@NonNull ItemShopBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
