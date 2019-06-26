package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ushop.ushopapp.Model.Product;

import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private EditText nameTextField,descriptionTextField,unitpriceTextField;
    private TextView uploadImage, cancel;
    private ImageView productImage;
    private Spinner categorySpinner,storeSpinner;
    private Button uploadProduct;
    private String name,description,category,store,imageURl;
    private double unitPrice;
    private FirebaseFirestore db;

    private Uri filePath;

    private static final String TAG = "AddProductActivity";
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
        setContentView(R.layout.activity_add_product);
        getSupportActionBar().setTitle("Add Product");

        //setup firebase dfirestore db
        db = FirebaseFirestore.getInstance();

        //setup firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        // setup Image storage location
        storageLocation = "gs://ushop-73f4b.appspot.com/productImages/";

        nameTextField = findViewById(R.id.nameTextFieldAddProduct);
        descriptionTextField = findViewById(R.id.descriptionTextFieldAddProduct);
        unitpriceTextField = findViewById(R.id.unitpriceTextFieldAddProduct);
        categorySpinner = findViewById(R.id.spinner_categoryAddProduct);
        storeSpinner = findViewById(R.id.spinner_storeAddProduct);
        productImage = findViewById(R.id.imageViewAddProduct);
        uploadImage = findViewById(R.id.uploadImageAddProduct);
        uploadProduct = findViewById(R.id.uploadProduct);
        cancel = findViewById(R.id.addProductCancelButton);

        //Spinner for category selection
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.category));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        //Spinner for store selection
        ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.store));
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        uploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                // validate empty fields
                if(!validate()){
                    return;
                }else{
                addProduct();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductActivity.this.finish();
            }
        });

    }

    public void addProduct() {
        //Add Product is it has image file
        if(filePath != null){
            uploadImage();
        }else{
            Toast.makeText(AddProductActivity.this, "Select an Image", Toast.LENGTH_LONG).show();
        }
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

    //Function -> Upload image to firebase storage
    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();
        if(filePath != null) {
            //product name is picture file name
            String _name = nameTextField.getText().toString().trim();
            String _store = storeSpinner.getSelectedItem().toString();
            System.out.println(_store);
            //upload image to firebase storage
                ref = storageReference.child("productImages/" + _name + _store);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Successfull Upload then get image URL to save in product
                            getImageURl();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddProductActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    // Function -> get image url for image file
    private void getImageURl(){
        String _name = nameTextField.getText().toString();
        String _store = storeSpinner.getSelectedItem().toString();

        StorageReference gfReference = storage.getReferenceFromUrl(storageLocation+_name+_store);

        gfReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Log.d("URI", String.valueOf(uri));

                // Get product value;
                name = nameTextField.getText().toString();
                description = descriptionTextField.getText().toString();
                unitPrice = Double.parseDouble(unitpriceTextField.getText().toString());
                category = categorySpinner.getSelectedItem().toString();
                store = storeSpinner.getSelectedItem().toString();

                //set Image URL
                imageURl = String.valueOf(uri);

                // make new product
                Product newProduct = new Product(name,description,unitPrice,category,store,imageURl);

                //upload the product into firestore
                productFirestoreUpload(newProduct);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("URI", "Error");
            }
        });

    }

    // Function -> Upload Product into database(Firestore)
    private void productFirestoreUpload(Product product) {
        // progress bar
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Product");
        progressDialog.show();

        //Add a new document with a generated ID
        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        AddProductActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddProductActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Validate empty fields
    private boolean validate(){
        boolean valid = true;

        String _name = nameTextField.getText().toString();
        String _description = descriptionTextField.getText().toString();
        String _price = unitpriceTextField.getText().toString();
        String _category = categorySpinner.getSelectedItem().toString();
        String _store = storeSpinner.getSelectedItem().toString();


        //validate name
        if (_name.isEmpty() || _name.length() < 2){
            valid = false;
            nameTextField.setError("at least 2 characters");
        }else{
            nameTextField.setError(null);
        }

        //validate description
        if (_description.isEmpty() || _description.length() < 5){
            valid = false;
            descriptionTextField.setError("at least 5 characters");
        }else{
            descriptionTextField.setError(null);
        }

        //validate empty unit price
        if (_price.isEmpty()){
            valid = false;
            unitpriceTextField.setError("enter value");
        }else{
            unitpriceTextField.setError(null);
        }

        if(_store.equals("Select Store")){ //Validate store
            valid = false;
            Toast.makeText(AddProductActivity.this, "Select store", Toast.LENGTH_LONG).show();
        }

        //validate category
        if (_category.equals("Select Category")){
            valid = false;
            Toast.makeText(AddProductActivity.this, "Select category", Toast.LENGTH_LONG).show();
        }

        return valid;

    }
}
