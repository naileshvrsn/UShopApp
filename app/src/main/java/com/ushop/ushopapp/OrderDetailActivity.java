package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderDetailActivity extends AppCompatActivity {

    String orderId,shippingAddress;

    TextView userName,userAddress,orderSubtotal,orderShipping,orderDiscount,orderTotal;

    private FirebaseFirestore db;
    private CollectionReference orderProducts;
    private DocumentReference orderDetials;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private CartItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Bundle extrasfromIntent = getIntent().getExtras();
        orderId = extrasfromIntent.getString("orderId");


        userName = findViewById(R.id.order_detail_user_name);
        userAddress = findViewById(R.id.order_detail_user_address);
        orderSubtotal = findViewById(R.id.order_detail_subTotal);
        orderShipping = findViewById(R.id.order_detail_shipping);
        orderDiscount = findViewById(R.id.order_detail_discount);
        orderTotal = findViewById(R.id.order_detail_total);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        orderDetials = db.collection("order")
                .document(currentUser.getUid())
                .collection("orders")
                .document(orderId);

        orderDetials.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Order order = document.toObject(Order.class);
                        userName.setText(order.getName());
                        //shipping Address
                        shippingAddress = order.getStreet()+"\n"+order.getSuburb()+"\n"+order.getCity()+", "+ order.getPostalCode();
                        userAddress.setText(shippingAddress);
                        orderSubtotal.setText(" SubTotal: $ "+ String.valueOf(order.getSubtotal()));
                        orderShipping.setText(" Shipping: $ "+ String.valueOf(order.getShipping()));
                        orderDiscount.setText(" Discount: -$ "+ String.valueOf(order.getDiscount()));
                        orderTotal.setText(" Total: $ "+ String.valueOf(order.getTotal()));



                    } else {
                        Log.d("Error:", "No such document");
                    }
                } else {
                    Log.d("get failed with ", task.getException().getMessage());
                }
            }
        });

        orderProducts = orderDetials.collection("products");


       setUpRecyclerView();


    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<Cart> options = new FirestoreRecyclerOptions.Builder<Cart>()
                .setQuery(orderProducts, Cart.class)
                .build();

        adapter = new CartItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.order_detail_product_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
