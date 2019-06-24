package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfirmOrderActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmOrderActivity";


    private FirebaseAuth mauth;
    private String userid;
    private Double subtotal, discount, shipment, total;

    private FirebaseFirestore db;
    private DocumentReference docRef;
    private CollectionReference cartRef;
    private CollectionReference orderRef;

    BigDecimal bd;
    SweetAlertDialog pDialog;

    private TextView uName, uStreet, uSuburb, uCity, uPostCode, oSubTotal, oDiscount, oShipment, oTotal;


    private Button makePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        pDialog=new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);


        mauth = FirebaseAuth.getInstance();
        userid = mauth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(userid);
        cartRef = db.collection("cartList").document(userid).collection("products");
        orderRef = db.collection("order").document(userid).collection("orders");

        subtotal = getIntent().getDoubleExtra("cartTotal", 0);

        uName = findViewById(R.id.user_name_txt);
        uStreet = findViewById(R.id.user_street_txt);
        uSuburb = findViewById(R.id.user_suburb_txt);
        uCity = findViewById(R.id.user_city_txt);
        uPostCode = findViewById(R.id.user_postCode_txt);
        makePayment = findViewById(R.id.placeOrder_btn_order_page);
        oSubTotal = findViewById(R.id.sub_total__order_page);
        oDiscount = findViewById(R.id.discount_order_page);
        oShipment = findViewById(R.id.shipping_order_page);
        oTotal = findViewById(R.id.order_total_order_page);

        setUserDetails();
        setOrderDetails();

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();

                if (validate()) {
                    makeOrderPayment();

                    placeOrder();


                } else {

                }
            }
        });

    }

    private void setUserDetails() {

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    User user = document.toObject(User.class);

                    //Set values to the field
                    uName.setText(user.getName());
                    uStreet.setText(user.getStreet());
                    uSuburb.setText(user.getSuburb());
                    uCity.setText(user.getCity());
                    uPostCode.setText(user.getPostCode());
                } else {
                    Toast.makeText(ConfirmOrderActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setOrderDetails() {
        shipment = 6.99;
        discount = 0.0;

        total = (subtotal + shipment) - discount;

        oSubTotal.setText(String.valueOf(subtotal));
        oDiscount.setText(String.valueOf(discount));
        oShipment.setText(String.valueOf(shipment));

        bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        oTotal.setText(String.valueOf(bd.doubleValue()));

    }

    private boolean validate() {
        boolean valid = true;

        //Textfields for validation;
        TextView _uName, _uStreet, _uSuburb, _uCity, _uPostCode;
        _uName = findViewById(R.id.user_name_txt);
        _uStreet = findViewById(R.id.user_street_txt);
        _uSuburb = findViewById(R.id.user_suburb_txt);
        _uCity = findViewById(R.id.user_city_txt);
        _uPostCode = findViewById(R.id.user_postCode_txt);

        if (_uName.getText().toString() == "" || _uName.getText().length() < 2) {
            valid = false;
            _uName.setError("Name must be atleast 2 Character");
        } else {
            _uName.setError(null);
        }

        if (_uStreet.getText().toString() == "" || _uStreet.getText().length() < 5) {
            valid = false;
            _uStreet.setError("Name must be atleast 5 Character");
        } else {
            _uStreet.setError(null);
        }

        if (_uSuburb.getText().toString() == "" || _uSuburb.getText().length() < 5) {
            valid = false;
            _uSuburb.setError("Name must be atleast 5 Character");
        } else {
            _uSuburb.setError(null);
        }

        if (_uCity.getText().toString() == "" || _uCity.getText().length() < 4) {
            valid = false;
            _uCity.setError("Please enter your city");
        } else {
            _uCity.setError(null);
        }

        if (_uPostCode.getText().toString() == "" || _uPostCode.getText().length() != 4) {
            valid = false;
            _uPostCode.setError("Name must be 4 numbers only");
        } else {
            _uPostCode.setError(null);
        }
        return valid;
    }


    private void makeOrderPayment() {

    }

    private void placeOrder() {
        pDialog.show();


        //date of the order
        Date callForDate = new Date();

        //put in other values into the Order
        //Shipping details
        String orderUserName = uName.getText().toString();
        String orderUserStreet = uStreet.getText().toString();
        String orderUserSuburb = uSuburb.getText().toString();
        String orderUserCity = uCity.getText().toString();
        String orderUserPostalCode = uPostCode.getText().toString();
        //Order Details
        double orderSubTotal = Double.valueOf(oSubTotal.getText().toString());
        double orderShipping = Double.valueOf(oShipment.getText().toString());
        double orderDiscount = Double.valueOf(oDiscount.getText().toString());
        double orderTotal = Double.valueOf(oTotal.getText().toString());
        //Order Status
        String orderStatus = "Pending payment";

        // make new blank order
        Order order = new Order(callForDate,orderUserName,orderUserStreet,orderUserSuburb,orderUserCity,orderUserPostalCode,orderSubTotal,orderShipping,orderDiscount,orderTotal,orderStatus);

        //set cart list
        orderRef.add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                addProductsToOrder(documentReference.getId());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });


    }

    private void addProductsToOrder(String id) {
        final CollectionReference productRef = orderRef.document(id).collection("products");

        // get cart list

        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Cart product = documentSnapshot.toObject(Cart.class);
                        //add all products to the order for future reference
                        productRef.document(product.getPid()).set(product)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                        } else {

                                        }
                                    }
                                });
                    }
                    //dismiss progress dialog
                    pDialog.dismissWithAnimation();

                    // show alert message
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ConfirmOrderActivity.this);
                    dlgAlert.setMessage("Order Placed Successfully");
                    dlgAlert.setTitle("SUCCESS");
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gotoselectstore();
                        }
                    });
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                }

            }
        });


    }

    public void gotoselectstore() {
        Intent i = new Intent(ConfirmOrderActivity.this, SelectStoreActivity.class);
        startActivity(i);
        ConfirmOrderActivity.this.finish();
    }
}