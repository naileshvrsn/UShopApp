package com.ushop.ushopapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private EditText nameTextField,descriptionTextField,unitpriceTextField,categoryTextField,storeTextField;
    private ImageView productImage;
    private Button uploadImage,uploadProduct;
    private String name,description,category,store;
    private double unitPrice;
    private FirebaseFirestore db;

    private Uri filePath;

    private static final String TAG = "AddProductActivity";
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();

        productImage = findViewById(R.id.imageView);
        nameTextField = findViewById(R.id.nameTextField);
        descriptionTextField = findViewById(R.id.descriptionTextField);
        unitpriceTextField = findViewById(R.id.unitpriceTextField);
        categoryTextField = findViewById(R.id.categotyTextField);
        storeTextField = findViewById(R.id.storeTextField);
        uploadImage = findViewById(R.id.uploadImage);
        uploadProduct = findViewById(R.id.uploadProduct);


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        uploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
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

    public void addProduct(){

        

    }
}
