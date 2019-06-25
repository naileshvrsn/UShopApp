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
import com.google.firebase.firestore.DocumentSnapshot;


public class OrderAdapter extends FirestoreRecyclerAdapter <Order, OrderAdapter.orderHolder>{

    private OnItemClickListener listener;

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull orderHolder orderHolder, int i, @NonNull Order order) {

        String shippingAddress = "";
        shippingAddress = order.getName() +"\n"+order.getStreet()+"\n"
                +order.getSuburb()+"\n"+order.getCity()+", "+order.getPostalCode();

        orderHolder.orderTotalItems.setText(order.getProductsCount() + " Products");
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position));
                    }

                }
            });

        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
