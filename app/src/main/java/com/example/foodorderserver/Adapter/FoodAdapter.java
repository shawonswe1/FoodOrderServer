package com.example.foodorderserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderserver.R;
import com.example.foodorderserver.model.AddFood;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyFoodViewholder> {

    Context context;
    List<AddFood> addFoodList;

    public FoodAdapter(Context context , List<AddFood> addFoodList) {
        this.context = context;
        this.addFoodList = addFoodList;
    }

    @NonNull
    @Override
    public MyFoodViewholder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.food_list,parent,false);

        return new MyFoodViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyFoodViewholder holder , int position) {

        AddFood addFood = addFoodList.get(position);

        holder.name.setText(addFood.getFoodName());
        holder.price.setText(addFood.getFoodPrice());
        holder.discount.setText(addFood.getFoodDiscount());

        Picasso.with(context).load(addFood.getImage())
                .placeholder(R.drawable.picture)
                .fit().centerCrop().into(holder.image);

        Glide.with(context).load(addFood.getImage())
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.picture)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return addFoodList.size();
    }

    class MyFoodViewholder extends RecyclerView.ViewHolder
    {

        TextView name,price,discount;
        ImageView image;

        public MyFoodViewholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.foodNameText);
            price = itemView.findViewById(R.id.foodPriceText);
            discount = itemView.findViewById(R.id.foodDiscountText);
            image = itemView.findViewById(R.id.foodImageVew);
        }
    }
}
