package com.ushop.ushopapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ushop.ushopapp.Adapter.OrderAdapter;
import com.ushop.ushopapp.Model.Order;

public class OrderListActivity extends AppCompatActivity {


    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ordersRef = db.collection("order")
            .document(user.getUid()).collection("orders");

    private OrderAdapter adapter;

    private static final String TAG = "CheckoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        Query squery = ordersRef.orderBy("orderDate", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Order> options = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(squery, Order.class).build();

        adapter = new OrderAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.order_Recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                String orderId = documentSnapshot.getId();

                Bundle extrastoSend = new Bundle();
                extrastoSend.putString("orderId", orderId);
                startActivity(new Intent(getBaseContext(), OrderDetailActivity.class).putExtras(extrastoSend));
            }
        });
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
