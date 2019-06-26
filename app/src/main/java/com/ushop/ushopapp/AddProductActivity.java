package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ushop.ushopapp.Model.Product;

import java.io.IOException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

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
    SweetAlertDialog pDialog;

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


        nameTextField = findViewById(R.id.nameTextFieldAddProduct);
        descriptionTextField = findViewById(R.id.descriptionTextFieldAddProduct);
        unitpriceTextField = findViewById(R.id.unitpriceTextFieldAddProduct);
        categorySpinner = findViewById(R.id.spinner_categoryAddProduct);
        storeSpinner = findViewById(R.id.spinner_storeAddProduct);
        productImage = findViewById(R.id.imageViewAddProduct);
        uploadImage = findViewById(R.id.uploadImageAddProduct);
        uploadProduct = findViewById(R.id.uploadProduct);
        cancel = findViewById(R.id.addProductCancelButton);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Adding Product");
        pDialog.setCancelable(false);

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

            productFirestoreUpload();

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

    // Function -> Upload Product into database(Firestore)
    private void productFirestoreUpload() {
        // progress bar
        pDialog.show();

        // Get product value;
        name = nameTextField.getText().toString();
        description = descriptionTextField.getText().toString();
        unitPrice = Double.parseDouble(unitpriceTextField.getText().toString());
        category = categorySpinner.getSelectedItem().toString();
        store = storeSpinner.getSelectedItem().toString();

        Product product = new Product(name,description,unitPrice,category,store);

        //Add a new document with a generated ID
        db.collection("products")
                .add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    uploadImage(task.getResult().getId());
                }else {
                    Toast.makeText(AddProductActivity.this, "Product not uploaded ",Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(AddProductActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Function -> Upload image to firebase storage
    private void uploadImage(final String productId) {

        if(filePath != null) {

            ref = storageReference.child("productImages/"+productId );
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getImageURl(productId);
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
    private void getImageURl(final String productID){
        storageLocation = "gs://ushop-73f4b.appspot.com/productImages/";
        StorageReference gfReference = storage.getReferenceFromUrl(storageLocation+productID);

        gfReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Log.d("URI", String.valueOf(uri));

                imageURl = String.valueOf(uri);

                Product product = new Product();
                product.setImageLocation(imageURl);

                //get product and set image Url
                db.collection("products").document(productID).update("imageLocation",imageURl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismissWithAnimation();
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(AddProductActivity.this);
                        dlgAlert.setMessage("Product Added Succeffully");
                        dlgAlert.setTitle("SUCCESS");
                        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gotoHome();
                            }
                        });
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("URI", "Error");
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

    public void gotoHome() {
        Intent i = new Intent(AddProductActivity.this, HomeActivity.class);
        startActivity(i);
        AddProductActivity.this.finish();
    }
}
