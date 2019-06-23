package com.ushop.ushopapp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView pnameTxt, priceTxt, pquantityTxt;
    private RecyclerView.OnClickListener itemClickListener;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);

        pnameTxt = itemView.findViewById(R.id.cart_product_name);
        priceTxt = itemView.findViewById(R.id.cart_product_price);
        pquantityTxt = itemView.findViewById(R.id.cart_product_quantity);


    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v);
    }

    public void setItemClickListener(RecyclerView.OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
