package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ushop.ushopapp.Model.User;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView userImage, editImage;
    private TextView userName, cancel;
    private EditText email, dob, phone, street, suburb, postcode, password, confirmPassword;
    private Button saveChanges;
    private String imageURl;
    private AppCompatAutoCompleteTextView city;
    private String[] cities;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDb;
    private DocumentReference documentReference;
    private User currentUserFirestore;

    //Firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;

    // Image storage URL string
    private String storageLocation;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Edit Profile");

        SweetAlertDialog pDialog = new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(true);
        pDialog.show();

        cities = getResources().getStringArray(R.array.nz_cities_array);
        editImage = findViewById(R.id.userProfileEditImage);
        userImage = findViewById(R.id.editProfileImageView);
        userName = findViewById(R.id.userProfileName);
        cancel = findViewById(R.id.userProfileCancelButton);
        email = findViewById(R.id.userProfileEmail);
        dob = findViewById(R.id.userProfileDateOfBirth);
        phone = findViewById(R.id.userProfilePhone);
        street = findViewById(R.id.userProfileStreet);
        suburb = findViewById(R.id.userProfileSuburb);
        city = findViewById(R.id.userProfileCity);
        postcode = findViewById(R.id.userProfilePostCode);
        password = findViewById(R.id.userProfileNewPassword);
        confirmPassword = findViewById(R.id.userProfileConfirmPassword);
        saveChanges = findViewById(R.id.userProfileSaveChangesButton);

        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
        documentReference = firestoreDb.collection("users").document(mAuth.getUid());
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //setup firebase storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // setup Image storage location
        storageLocation = "gs://ushop-73f4b.appspot.com/userImages/";

        email.setEnabled(false);
        dob.setEnabled(false);

        city.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, cities));
        city.setThreshold(1);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentUserFirestore = document.toObject(User.class);

                        Picasso.get().load(currentUserFirestore.getUserImageLocation()).into(userImage);
                        userName.setText(currentUserFirestore.getName());
                        email.setText(mAuth.getCurrentUser().getEmail());
                        DateFormat df = new SimpleDateFormat("MM/dd/yyy");
                        String currentUserDob = df.format(currentUserFirestore.getDateOfBirth());
                        dob.setText(currentUserDob);
                        phone.setText(currentUserFirestore.getPhone());
                        street.setText(currentUserFirestore.getStreet());
                        suburb.setText(currentUserFirestore.getSuburb());
                        city.setText(currentUserFirestore.getCity());
                        postcode.setText(currentUserFirestore.getPostCode());
                    } else {
                        //could not get user from firestore database hence back to login screen
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        UserProfileActivity.this.finish();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHome();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newphone = phone.getText().toString();
                String newstreet = street.getText().toString();
                String newsuburb = suburb.getText().toString();
                String newcity = city.getText().toString();
                String newpostcode = postcode.getText().toString();
                String newPassword = password.getText().toString();

                //validation failed from inputs
                if (!validate()) {
                    return;
                }

                else {
                    //if user wants to change password
                    if (!newPassword.isEmpty()) {
                        currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT);
                                } else {
                                    Toast.makeText(UserProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                                }
                            }
                        });
                    }

                    //save changes from text fields
                    final DocumentReference currentUserRef = firestoreDb.collection("users").document(mAuth.getUid());
                    currentUserRef.update("phone", newphone, "street", newstreet, "suburb", newsuburb, "city", newcity, "postCode", newpostcode)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UserProfileActivity.this, "Profile details updated successfully", Toast.LENGTH_SHORT);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserProfileActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                                }
                            });

                    //image changed or not
                    if (filePath != null) {
                        System.out.println(String.valueOf(filePath));
                        uploadImage(currentUserRef.getId());
                    }

                    showSuccessMessage();
                }
            }
        });

        pDialog.dismissWithAnimation();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(final String userId) {

        if (filePath != null) {

            ref = storageReference.child("userImages/" + userId);
            //upload the new image
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getImageURl(userId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "No image", Toast.LENGTH_LONG).show();
        }

    }

    private void getImageURl(final String userID) {

        StorageReference gfReference = storage.getReferenceFromUrl(storageLocation + userID);

        gfReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Log.d("URI", String.valueOf(uri));

                imageURl = String.valueOf(uri);

                //get user and set image Url
                firestoreDb.collection("users").document(userID).update("userImageLocation", imageURl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "Failed to save image to databasde", Toast.LENGTH_SHORT).show();
                        Log.d("URI", "Error");
                    }
                });
            }
        });
    }


    public boolean validate() {
        boolean valid = true;

        String uphone = phone.getText().toString();
        String ustreet = street.getText().toString();
        String usuburb = suburb.getText().toString();
        String ucity = city.getText().toString();
        String upostcode = postcode.getText().toString();
        String upassword = password.getText().toString().trim();
        String uconfirmPassword = confirmPassword.getText().toString().trim();

        if (uphone.isEmpty() || uphone.length() < 8 || uphone.length() > 12){
            phone.setError("Enter a valid phone number");
            valid = false;
        }
        else {
            phone.setError(null);
        }

        if (ustreet.isEmpty() || ustreet.length() < 5) {
            street.setError("at least 5 characters");
            valid = false;
        } else {
            street.setError(null);
        }

        if (usuburb.isEmpty() || usuburb.length() < 4) {
            suburb.setError("at least 4 characters");
            valid = false;
        } else {
            suburb.setError(null);
        }

        if (ucity.isEmpty() || ucity.length() < 6) {
            city.setError("at least 6 characters");
            valid = false;
        } else {
            city.setError(null);
        }

        if (upostcode.isEmpty() || upostcode.length() != 4) {
            postcode.setError("Enter a valid postcode");
            valid = false;
        } else {
            postcode.setError(null);
        }

        if (upassword.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if (!upassword.equals(uconfirmPassword)) {
            confirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }

        return valid;
    }

    public void gotoHome() {
        Intent i = new Intent(UserProfileActivity.this, HomeActivity.class);
        startActivity(i);
        UserProfileActivity.this.finish();
    }

    private void showSuccessMessage(){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(UserProfileActivity.this);
        dlgAlert.setMessage("Profile Updated Succeffully");
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

    @Override
    public void onBackPressed() {
        gotoHome();
    }
}
