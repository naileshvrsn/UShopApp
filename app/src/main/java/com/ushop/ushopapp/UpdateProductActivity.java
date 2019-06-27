package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ushop.ushopapp.Model.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

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
    SweetAlertDialog pDialog;

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
        getSupportActionBar().setTitle("Update Product");

        Bundle extrasfromIntent = getIntent().getExtras();
        productID = extrasfromIntent.getString("productID");

        //setup firebase dfirestore db
        db = FirebaseFirestore.getInstance();

        //setup firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // setup DialogBox
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Updating Product");
        pDialog.setCancelable(false);

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

        uploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    return;
                }else{
                    updateProduct(productID);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoProductList();
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

                        getSupportActionBar().setTitle("Update " + product.getName());
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

    private void updateProduct(final String productID) {
        pDialog.show();

        // Get product value;
        name = nameTextField.getText().toString();
        description = descriptionTextField.getText().toString();
        unitPrice = Double.parseDouble(unitpriceTextField.getText().toString());
        category = categorySpinner.getSelectedItem().toString();
        store = storeSpinner.getSelectedItem().toString();

        HashMap<String,Object> data = new HashMap<>();
        data.put("name",name);
        data.put("description",description);
        data.put("unitPrice",unitPrice);
        data.put("category",category);
        data.put("store",store);

        //Add a new document with a generated ID
        db.collection("products")
                .document(productID)
                .update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    uploadImage(productID);
                }
            }
        });
    }

    //Function -> Upload image to firebase storage
    private void uploadImage(final String productId) {

        if(filePath != null) {

            ref = storageReference.child("productImages/"+productId );
            //delete the previous image;
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //uploage the new image
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
                                    Toast.makeText(UpdateProductActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

        }
        else{
            pDialog.dismissWithAnimation();
            showMessage();
        }
    }

    private void getImageURl(final String productID){
        storageLocation = "gs://ushop-73f4b.appspot.com/productImages/";
        StorageReference gfReference = storage.getReferenceFromUrl(storageLocation+productID);

        gfReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Log.d("URI", String.valueOf(uri));

                imageURl = String.valueOf(uri);

                //get product and set image Url
                db.collection("products").document(productID).update("imageLocation",imageURl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismissWithAnimation();
                        showMessage();

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
            Toast.makeText(UpdateProductActivity.this, "Select store", Toast.LENGTH_LONG).show();
        }

        //validate category
        if (_category.equals("Select Category")){
            valid = false;
            Toast.makeText(UpdateProductActivity.this, "Select category", Toast.LENGTH_LONG).show();
        }

        return valid;
    }

    public void gotoProductList() {
        Intent i = new Intent(UpdateProductActivity.this, UpdateProductListActivity.class);
        startActivity(i);
        UpdateProductActivity.this.finish();
    }

    private void showMessage(){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(UpdateProductActivity.this);
        dlgAlert.setMessage("Product updated successfully");
        dlgAlert.setTitle("SUCCESS");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoProductList();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}
