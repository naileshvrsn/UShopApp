package com.ushop.ushopapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class OrderAdapter extends FirestoreRecyclerAdapter <Order, OrderAdapter.orderHolder>{

    DocumentReference orderRef;
    CollectionReference productsRef;
    int productCount = 0;

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull orderHolder orderHolder, int i, @NonNull Order order) {

        orderRef =  getSnapshots().getSnapshot(i).getReference();
        productsRef= orderRef.collection("products");


        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        productCount += 1;
                    }
                }
            }
        });


        String shippingAddress = "";
        shippingAddress = order.getName() +"\n"+order.getStreet()+"\n"
                +order.getSuburb()+"\n"+order.getCity()+", "+order.getPostalCode();

        orderHolder.orderTotalItems.setText(productCount);
        orderHolder.orderDate.setText(order.getOrderDate());
        orderHolder.orderTotal.setText("$ "+order.getTotal());
        orderHolder.orderShippingAddress.setText(shippingAddress);
        orderHolder.orderImageView.setImageResource(R.drawable.blank_user);
    }

    @NonNull
    @Override
    public orderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_order_item,parent,false);

        return new orderHolder(v);
    }

    class orderHolder extends RecyclerView.ViewHolder{

        TextView orderTotalItems,orderShippingAddress,orderDate,orderTotal;
        ImageView orderImageView;

        public orderHolder(@NonNull View itemView) {
            super(itemView);

            orderTotalItems = itemView.findViewById(R.id.order_total_items);
            orderShippingAddress = itemView.findViewById(R.id.order_shipping_address);
            orderDate = itemView.findViewById(R.id.order_placed_date);
            orderTotal = itemView.findViewById(R.id.order_total);
            orderImageView = itemView.findViewById(R.id.order_imageView);

        }
    }

}
