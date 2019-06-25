package com.ushop.ushopapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

public class OrderAdapter extends FirestoreRecyclerAdapter {
    class orderHolder extends RecyclerView.ViewHolder{

        TextView orderTotalItems,orderShippingAddress,orderDate,orderTotal;
        ImageView orderImageView;

        public orderHolder(@NonNull View itemView) {
            super(itemView);

            orderTotalItems = itemView.findViewById(R.id.order_total_items);
            orderShippingAddress = itemView.findViewById(R.id.order_shipping_address);
            orderDate = itemView.findViewById(R.id.order_shipping_address);
            orderTotal = itemView.findViewById(R.id.order_total);
            orderImageView = itemView.findViewById(R.id.order_imageView);

        }
    }

}
