package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    private ElegantNumberButton quantityButton;
    private String productID;
    private String store;
    FirebaseUser currentUser;
    Product currentProduct;

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
        Bundle extrasfromIntent = getIntent().getExtras();
        productID = extrasfromIntent.getString("productId");
        String nameProductName = extrasfromIntent.getString("productName");
        store = extrasfromIntent.getString("store");

        getSupportActionBar().setTitle("Shopping from " + store);
        switch (store){
            case "Countdown":
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.countdownBrightGreen)));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.countdownGreen));
                }
                break;

            case "PaknSave" :
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.paknsaveBrightYellow)));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.paknsaveYellow));
                }
                break;
            default:
        }

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
        quantityButton = findViewById(R.id.quantityButton);


        //get product from id
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        currentProduct = document.toObject(Product.class);

                        // display information
                        Picasso.get().load(currentProduct.getImageLocation()).into(productImage);
                        productName.setText(currentProduct.getName());
                        productPrice.setText("$ " + String.valueOf(currentProduct.getUnitPrice()));
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

        Cart cartProduct = new Cart();
        cartProduct.setPid(productID);
        cartProduct.setPname(productName.getText().toString());
        cartProduct.setPrice(productPrice.getText().toString());
        cartProduct.setQuantity(quantityButton.getNumber());
        cartProduct.setImageLocation(currentProduct.getImageLocation());

        cartListRef.document(currentUser.getUid()).
                collection("products").document(productID).set(cartProduct)
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
