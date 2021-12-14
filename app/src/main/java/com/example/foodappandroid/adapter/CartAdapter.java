package com.example.foodappandroid.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodappandroid.R;
import com.example.foodappandroid.databinding.ItemCartProductBinding;
import com.example.foodappandroid.model.FoodModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private onCartItemClickListener listener;
    private List<FoodModel> list;
    private float itemPrice;
    private int totalPrice;
    private int quantity;


    public CartAdapter(Context context, List<FoodModel> list, onCartItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartProductBinding binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FoodModel model = list.get(position);
        itemPrice = Float.parseFloat(model.getPrice());
        if (model.getQuantity() >= 1) {
            holder.binding.textFoodName.setText(model.getName() + " x " + model.getQuantity());
        } else {
            holder.binding.textFoodName.setText(model.getName());
        }
        for (int i = 1; i < model.getQuantity(); i++) {
            itemPrice = itemPrice + Float.parseFloat(model.getPrice());
        }
        holder.binding.textFoodPrice.setText("£" + String.valueOf(itemPrice));
        if (model.getQuantity() == 0) {
            //holder.binding.layoutQuantityControl.imageSub.setVisibility(View.GONE);
            holder.binding.layoutQuantityControl.textQuantity.setText("Add");
            holder.binding.textFoodPrice.setText("");
            holder.binding.textFoodPrice.setText("");
            holder.binding.layoutQuantityControl.imageSub.setImageResource(R.drawable.ic_minus);
            holder.binding.layoutQuantityControl.imageSub.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.binding.layoutQuantityControl.textQuantity.setText(Integer.toString(model.getQuantity()));
        } else if (model.getQuantity() < 0) {
            holder.binding.layoutQuantityControl.imageSub.setImageResource(R.drawable.ic_delete);
            holder.binding.layoutQuantityControl.imageSub.setScaleType(ImageView.ScaleType.CENTER);
            holder.binding.textFoodPrice.setText("");
        } else {
            holder.binding.layoutQuantityControl.imageSub.setImageResource(R.drawable.ic_minus);
            holder.binding.layoutQuantityControl.imageSub.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.binding.layoutQuantityControl.imageSub.setVisibility(View.VISIBLE);
            holder.binding.layoutQuantityControl.textQuantity.setText(Integer.toString(model.getQuantity()));
        }
        holder.binding.layoutQuantityControl.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onQuantityAdd(position);
            }
        });
        holder.binding.layoutQuantityControl.imageSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getQuantity() >= -1) {
                    if (model.getQuantity() == -1) {
                        listener.onDeleteItem(position);
                    } else {
                        listener.onQuantitySub(position);
                    }
                }
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

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private ItemCartProductBinding binding;

        public CartViewHolder(@NonNull ItemCartProductBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public interface onCartItemClickListener {
        void onItemClicked(int position);

        void onQuantityAdd(int position);

        void onQuantitySub(int position);

        void onDeleteItem(int position);
    }
}
