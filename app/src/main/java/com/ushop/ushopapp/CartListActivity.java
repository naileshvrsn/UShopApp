package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class CartListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference cartListRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CartItemAdapter adapter;
    TextView cart_TotalTxt;
    Button proccedtoCheckout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        cart_TotalTxt = findViewById(R.id.cart_Total_txt);
        proccedtoCheckout = findViewById(R.id.proceed_to_checkoutBtn);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        cartListRef = db.collection("cartList").document(currentUser.getUid()).collection("products");



        setUpRecyclerView();
        calculateCartPrice();


        proccedtoCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedToCheckout();
            }
        });

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


      new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
              ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
              return false;
          }

          @Override
          public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.removeProduct(viewHolder.getAdapterPosition());
                //update price after edit
              calculateCartPrice();
          }
      }).attachToRecyclerView(recyclerView);

      adapter.setOnItemClickListener(new CartItemAdapter.OnItemCLickListener() {
          @Override
          public void onItemClick(DocumentSnapshot documentSnapshot) {
            String productId = documentSnapshot.getId();
              Intent intent = new Intent(CartListActivity.this,ProductDetailActivity.class);
              intent.putExtra("productID",productId);
              startActivity(intent);

              CartListActivity.this.finish();
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

    // calculate the total price for the cart and display total price
    private void calculateCartPrice(){

        cartListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    double cartTotal = 0;
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        Cart product = documentSnapshot.toObject(Cart.class);

                        double productTotal = Double.valueOf(product.getPrice())
                                * Integer.valueOf(product.getQuantity());

                        cartTotal += productTotal;
                    }

                    BigDecimal bd = new BigDecimal(cartTotal).setScale(2, RoundingMode.HALF_UP);


                    cart_TotalTxt.setText("$ "+ bd.doubleValue());
                }

            }
        });

    }
    //
    private void proceedToCheckout() {
        //check if cart has item
        // done by using price
        Log.d("Total Price",cart_TotalTxt.getText().toString());

        if(cart_TotalTxt.getText().toString().equals("$ 0.0") ||
                cart_TotalTxt.getText().toString().equals("$ 0") ||
                cart_TotalTxt.getText().toString().equals("$ 0.00")){
            // show alert message
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Please select some products");
            dlgAlert.setTitle("No Products in the Cart!");

            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gotoselectstore();
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

        }else {
            Intent i = new Intent(CartListActivity.this,ConfirmOrderActivity.class);
            startActivity(i);
        }

    }

    public void gotoselectstore(){
        Intent i = new Intent(CartListActivity.this, SelectStoreActivity.class);
        startActivity(i);
        CartListActivity.this.finish();
    }
}
