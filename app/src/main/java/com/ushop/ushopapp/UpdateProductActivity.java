package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.ushop.ushopapp.Model.Product;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateProductActivity extends AppCompatActivity {


    private EditText nameTextField,descriptionTextField,unitpriceTextField;
    private TextView uploadImage, cancel;
    private ImageView productImage;
    private Spinner categorySpinner,storeSpinner;
    private Button uploadProduct;
    private String name,description,category,store,imageURl;
    private double unitPrice;
    private FirebaseFirestore db;
    private String productID;

    private Uri filePath;

    private static final String TAG = "UpdateProductActivity";
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;

    // Image storage URL string
    String storageLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        Bundle extrasfromIntent = getIntent().getExtras();
        productID = extrasfromIntent.getString("productID");

        //setup firebase dfirestore db
        db = FirebaseFirestore.getInstance();

        //setup firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        // setup Image storage location
        storageLocation = "gs://ushop-73f4b.appspot.com/productImages/";

        nameTextField = findViewById(R.id.nameTextFieldUpdateProduct);
        descriptionTextField = findViewById(R.id.descriptionTextFieldUpdateProduct);
        unitpriceTextField = findViewById(R.id.unitpriceTextFieldUpdateProduct);
        categorySpinner = findViewById(R.id.spinner_categoryUpdateProduct);
        storeSpinner = findViewById(R.id.spinner_storeUpdateProduct);
        productImage = findViewById(R.id.imageViewUpdateProduct);
        uploadImage = findViewById(R.id.uploadImageUpdateProduct);
        uploadProduct = findViewById(R.id.uploadProduct);
        cancel = findViewById(R.id.updateProductCancelButton);

        //Spinner for category selection
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        //Spinner for store selection
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.store));
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);


        //Setup product details
        setupProduct(productID);







        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    private void setupProduct(final String productID) {

        db.collection("products").document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Product product = document.toObject(Product.class);
                        // setup views with product

                        nameTextField.setText(product.getName());
                        descriptionTextField.setText(product.getDescription());
                        unitpriceTextField.setText(String.valueOf(product.getUnitPrice()));
                        categorySpinner.setSelection(((ArrayAdapter<String>)categorySpinner.getAdapter()).getPosition(product.getCategory()));
                        storeSpinner.setSelection(((ArrayAdapter<String>)storeSpinner.getAdapter()).getPosition(product.getStore()));
                        Picasso.get().load(product.getImageLocation()).into(productImage);






                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });





    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                productImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
