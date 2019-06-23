package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductListActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ShowProductActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

       getAllProducts();


    }
    public void getAllProducts() {
       final ArrayList<Product> productList = new ArrayList<>();

        db.collection("products")
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

            }
        });

    }




}
