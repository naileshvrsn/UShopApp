package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference docRef;
    private FirebaseAuth mAuth;

    private ImageView productImage;
    private TextView productName,productPrice,description;
    private Button addToCart;
    private ElegantNumberButton quanityButton;
    private String productID;
    FirebaseUser currentUser;


    private static final String TAG = "ProductDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        //extract product id
        productID = getIntent().getStringExtra("productID");

        //setup current User
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();


        //db setup
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("products").document(productID);

        //setup views
        productImage = findViewById(R.id.product_ImageView);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        description = findViewById(R.id.product_description);
        addToCart = findViewById(R.id.addtocart);
        quanityButton = findViewById(R.id.quantityButton);



        //get product from id
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Product currentProduct = document.toObject(Product.class);

                        // display information
                        Picasso.get().load(currentProduct.getImageLocation()).into(productImage);
                        productName.setText(currentProduct.getName());
                        productPrice.setText(String.valueOf(currentProduct.getUnitPrice()));
                        description.setText(currentProduct.getDescription());

                    }else {
                        Log.d(TAG,"No document found");
                    }
                }else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
        pDialog.dismissWithAnimation();


        //add to cart button clicked
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProducttoCart();

            }
        });

    }

    private void addProducttoCart() {

        //prog

        String saveCurrentDate,saveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        //date
        SimpleDateFormat currentDate = new SimpleDateFormat("dd.MMM.yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        //time
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        CollectionReference cartListRef = db.collection("cartList");

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",quanityButton.getNumber());



        cartListRef.document(currentUser.getUid()).
                collection("products").document(productID).set(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(ProductDetailActivity.this,"Product added to cart",Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(ProductDetailActivity.this,"Product not added to cart",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}

//cartListRef.document(currentUser.getUid()).
//        collection("products").document(productID).update(cartMap)
//        .addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if(task.isSuccessful()){
//        Toast.makeText(ProductDetailActivity.this,"Product added to cart",Toast.LENGTH_SHORT).show();
//        }else {
//        Toast.makeText(ProductDetailActivity.this,"Product not added to cart",Toast.LENGTH_SHORT).show();
//        }
//        }
//        });