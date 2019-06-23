package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CartListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference cartListRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CartItemAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        cartListRef = db.collection("cartList").document(currentUser.getUid()).collection("products");


        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
      FirestoreRecyclerOptions<Cart> options = new FirestoreRecyclerOptions.Builder<Cart>()
              .setQuery(cartListRef,Cart.class)
              .build();

      adapter = new CartItemAdapter(options);

      RecyclerView recyclerView = findViewById(R.id.cart_list);
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
