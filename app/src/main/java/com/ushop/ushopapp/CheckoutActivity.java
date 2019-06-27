package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.ushop.ushopapp.Config.Config;
import com.ushop.ushopapp.Model.Cart;
import com.ushop.ushopapp.Model.Order;
import com.ushop.ushopapp.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";

    //Paypal Setup
    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    String amount = "";

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
        setContentView(R.layout.activity_checkout);
        getSupportActionBar().setTitle("Confirm Order");

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Placing Order");
        pDialog.setCancelable(false);

        // Start Paypal Service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        mauth = FirebaseAuth.getInstance();
        userid = mauth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(userid);
        cartRef = db.collection("cartList").document(userid).collection("products");
        orderRef = db.collection("order").document(userid).collection("userorders");

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
                } else {
                    return;
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
                    Toast.makeText(CheckoutActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setOrderDetails() {
        shipment = 6.99;
        discount = 5.0;

        total = (subtotal + shipment) - discount;

        oSubTotal.setText(String.valueOf(subtotal));
        oDiscount.setText("-" + String.valueOf(discount));
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

        amount = oTotal.getText().toString();

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "NZD",
                "Make Order Payment", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        JSONObject jsonObject = confirmation.toJSONObject();
                        // Checkpayment Status
                        checkPaymentStatus(jsonObject.getJSONObject("response"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPaymentStatus(JSONObject response) {
        try {
            String txtId = response.getString("id");
            String status = response.getString("state");
            if (!txtId.isEmpty() && status.equals("approved")) {
                placeOrder(txtId);
            } else {
                //show alert message if payment unsuccessfull
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CheckoutActivity.this);
                dlgAlert.setMessage("Payment Unsuccessfull");
                dlgAlert.setTitle("Error");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PayPalService.class));
    }

    private void placeOrder(String paymentId) {
        pDialog.show();
        final String orderpaymentId = paymentId;
        cartRef.get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                             int count = task.getResult().size();

                             //date of the order
                             SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                             String orderDate = simpleDateFormat.format(new Date());

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
                             String orderStatus = "New Order";

                             // make new blank order
                             Order order = new Order(orderDate, orderUserName, orderUserStreet, orderUserSuburb, orderUserCity, orderUserPostalCode,
                                     orderSubTotal, orderShipping, orderDiscount, orderTotal, count, orderStatus, orderpaymentId);

                             //set cart list
                             orderRef.add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                 @Override
                                 public void onSuccess(DocumentReference documentReference) {
                                     addProductsToOrder(documentReference.getId());
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Log.w(TAG, "Error adding document", e);
                                 }
                             });
                         }
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
                        productRef.document(documentSnapshot.getId()).set(product)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                        }
                                    }
                                });
                    }
                    //dismiss progress dialog
                    pDialog.dismissWithAnimation();
                    clearCart();
                    // show alert message
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CheckoutActivity.this);
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

    private void clearCart() {
        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        cartRef.document(documentSnapshot.getId())
                                .delete()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error Clearing Cart", e);
                                    }
                                });
                    }
                }
            }
        });
    }

    public void gotoselectstore() {
        Intent i = new Intent(CheckoutActivity.this, SelectStoreActivity.class);
        startActivity(i);
        CheckoutActivity.this.finish();
    }

}