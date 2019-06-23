package com.ushop.ushopapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class CartItemAdapter extends FirestoreRecyclerAdapter<Cart,CartItemAdapter.productHolder> {

    private OnItemCLickListener listener;

    public CartItemAdapter(@NonNull FirestoreRecyclerOptions<Cart> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull productHolder productHolder, int i, @NonNull Cart cart) {
        productHolder.pnametxt.setText(cart.getPname());
        productHolder.pricetxt.setText(cart.getPrice());
        productHolder.pquantitytxt.setText("Quntity: "+cart.getQuantity());
    }

    @NonNull
    @Override
    public productHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);

        return new productHolder(view);
    }


    public void removeProduct(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }



    class productHolder extends RecyclerView.ViewHolder{

        TextView pnametxt,pricetxt,pquantitytxt;


        public productHolder(@NonNull View itemView) {
            super(itemView);

            pnametxt = itemView.findViewById(R.id.cart_product_name);
            pricetxt = itemView.findViewById(R.id.cart_product_price);
            pquantitytxt = itemView.findViewById(R.id.cart_product_quantity);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION  && listener !=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position));
                    }
                }
            });
        }

    }

    public interface OnItemCLickListener{
        void onItemClick(DocumentSnapshot documentSnapshot);
    }
    public void setOnItemClickListener(OnItemCLickListener listener){
        this.listener =  (OnItemCLickListener) listener;
    }

}
