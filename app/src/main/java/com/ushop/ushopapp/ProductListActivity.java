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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductListActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ShowProductActivity";

    private String store;
    private String category;
    private FloatingActionButton cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        cartBtn = findViewById(R.id.cartViewbtn);

        Bundle extras = getIntent().getExtras();
        store = extras.getString("store");
        category = extras.getString("category");

        getSupportActionBar().setTitle(store + " " + category);

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

        getAllProducts();

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductListActivity.this,CartListActivity.class);
                startActivity(i);
            }
        });

    }


    public void getAllProducts() {
       final ArrayList<Product> productList = new ArrayList<>();

        db.collection("products")
                .whereEqualTo("store", store)
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Product product = document.toObject(Product.class);
                                product.setProductId(document.getId());
                                Log.d("Product id",product.getProductId());
                                productList.add(product);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        displayProducts(productList);
                    }
                });
    }

    public void displayProducts(final ArrayList<Product> products){

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        ProductAdapter productAdapter = new ProductAdapter(this, products);
        ListView listView = findViewById(R.id.productlist);
        listView.setAdapter(productAdapter);
        pDialog.dismissWithAnimation();

        // go to detailed page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Product selectedProduct = products.get(position);

                Log.d("Product id",selectedProduct.getProductId());
                Intent i = new Intent(getBaseContext(),ProductDetailActivity.class);
                i.putExtra("productID",selectedProduct.getProductId());
                startActivity(i);
                //ProductListActivity.this.finish();

            }
        });


    }




}
