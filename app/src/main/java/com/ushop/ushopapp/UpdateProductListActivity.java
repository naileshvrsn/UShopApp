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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.ushop.ushopapp.Adapter.ProductAdapter;
import com.ushop.ushopapp.Model.Product;

import java.util.ArrayList;
import java.util.Collection;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateProductListActivity extends AppCompatActivity {

    private static final String TAG = "UpdateProductList";

    private FirebaseFirestore db;
    private CollectionReference productRef;
    SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_list);

        pDialog= new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Uploading");
        pDialog.setCancelable(false);
        pDialog.show();

        db = FirebaseFirestore.getInstance();
        productRef = db.collection("products");

        getAllProducts();
        pDialog.dismissWithAnimation();
    }

    private void getAllProducts() {
        final ArrayList<Product> products = new ArrayList<>();

        productRef//.orderBy("name", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document:task.getResult()){
                        Product product = document.toObject(Product.class);
                        product.setProductId(document.getId());
                        products.add(product);
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                displayProducts(products);
            }
        });




    }

    public void displayProducts(final ArrayList<Product> products){

        Log.d(TAG,"Product Count " + products.size());




        ProductAdapter productAdapter = new ProductAdapter(this, products);
        ListView listView = findViewById(R.id.update_product_list);
        listView.setAdapter(productAdapter);
        pDialog.dismissWithAnimation();

        // go to detailed page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = products.get(position);
                if(selectedProduct != null){
                    Log.d("Selected Product", selectedProduct.getProductId());
                }else{
                    Log.d("Selected Product", "No Id");
                }


                Bundle extrastoSend = new Bundle();
                extrastoSend.putString("productID", selectedProduct.getProductId());
                startActivity(new Intent(getBaseContext(), UpdateProductActivity.class).putExtras(extrastoSend));
            }
        });


    }
}
