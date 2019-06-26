package com.ushop.ushopapp;

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
import com.squareup.picasso.Picasso;

public class CartItemAdapter extends FirestoreRecyclerAdapter<Cart,CartItemAdapter.productHolder> {

    private OnItemCLickListener listener;

    public CartItemAdapter(@NonNull FirestoreRecyclerOptions<Cart> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull productHolder productHolder, int i, @NonNull Cart cart) {
        Picasso.get().load(cart.getImageLocation()).into(productHolder.pimage);
        productHolder.pnametxt.setText(cart.getPname());
        productHolder.pricetxt.setText("$ "+ cart.getPrice());
        productHolder.pquantitytxt.setText(cart.getQuantity());


    }

    @NonNull
    @Override
    public productHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cart_item,parent,false);

        return new productHolder(view);
    }


    public void removeProduct(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }



    class productHolder extends RecyclerView.ViewHolder{

        TextView pnametxt,pricetxt,pquantitytxt;
        ImageView pimage;


        public productHolder(@NonNull View itemView) {
            super(itemView);

            pnametxt = itemView.findViewById(R.id.cart_product_name);
            pricetxt = itemView.findViewById(R.id.cart_product_price);
            pquantitytxt = itemView.findViewById(R.id.cart_product_quantity);
            pimage = itemView.findViewById(R.id.cart_product_image);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION  && listener !=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }

    }

    public interface OnItemCLickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemCLickListener listener){
        this.listener =  (OnItemCLickListener) listener;
    }

}
