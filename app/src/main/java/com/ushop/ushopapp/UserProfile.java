package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserProfile extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName, cancel;
    private EditText email, dob, street, suburb, city, postcode, password, confirmPassword;
    private Button saveChanges;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestoreDb;
    private DocumentReference documentReference;
    private User currentUserFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Edit Profile");
        SweetAlertDialog pDialog = new SweetAlertDialog(UserProfile.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading...");
        pDialog.show();

        userImage = findViewById(R.id.editProfileImageView);
        userName = findViewById(R.id.userProfileName);
        cancel = findViewById(R.id.userProfileCancelButton);
        email = findViewById(R.id.userProfileEmail);
        dob = findViewById(R.id.userProfileDateOfBirth);
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

        email.setEnabled(false);
        dob.setEnabled(false);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        currentUserFirestore = document.toObject(User.class);
                        userName.setText(currentUserFirestore.getName());
                        email.setText(mAuth.getCurrentUser().getEmail());
                        DateFormat df = new SimpleDateFormat("MM/dd/yyy");
                        String currentUserDob = df.format(currentUserFirestore.getDateOfBirth());
                        dob.setText(currentUserDob);
                        street.setText(currentUserFirestore.getStreet());
                        suburb.setText(currentUserFirestore.getSuburb());
                        city.setText(currentUserFirestore.getCity());
                        postcode.setText(currentUserFirestore.getPostCode());
                    }
                    else {
                        //could not get user from firestore database hence back to login screen
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        UserProfile.this.finish();
                    }
                }
                else {
                    Toast.makeText(UserProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        pDialog.dismissWithAnimation();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile.this.finish();
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress bar while activity loads
                SweetAlertDialog pDialog = new SweetAlertDialog(UserProfile.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("Saving...");
                pDialog.setCancelable(false);
                pDialog.show();

                String newstreet = street.getText().toString();
                String newsuburb = suburb.getText().toString();
                String newcity = city.getText().toString();
                String newpostcode = postcode.getText().toString();
                String newPassword = password.getText().toString();

                //validation failed from inputs
                if(!validate()){
                    pDialog.dismissWithAnimation();
                    return;
                }
                else {
                    //if user wants to change password
                    if (!newPassword.isEmpty()) {
                        currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(UserProfile.this, "Password updated successfully", Toast.LENGTH_LONG);
                                }
                                else {
                                    Toast.makeText(UserProfile.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                                }
                            }
                        });
                    }

                    else {
                        DocumentReference currentUserRef = firestoreDb.collection("users").document(mAuth.getUid());
                        currentUserRef.update("street", newstreet, "suburb", newsuburb, "city", newcity, "postCode", newpostcode)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserProfile.this, "Successfully updated profile ", Toast.LENGTH_LONG).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
                pDialog.dismissWithAnimation();
            }
        });

    }


    public boolean validate() {
        boolean valid = true;

        String ustreet = street.getText().toString();
        String usuburb = suburb.getText().toString();
        String ucity = city.getText().toString();
        String upostcode = postcode.getText().toString();
        String upassword = password.getText().toString().trim();
        String uconfirmPassword = confirmPassword.getText().toString().trim();

        if (ustreet.isEmpty() || ustreet.length() < 5){
            street.setError("at least 5 characters");
            valid = false;
        } else {
            street.setError(null);
        }

        if (usuburb.isEmpty() || usuburb.length() < 4){
            suburb.setError("at least 4 characters");
            valid = false;
        } else {
            suburb.setError(null);
        }

        if (ucity.isEmpty() || ucity.length() < 6){
            city.setError("at least 6 characters");
            valid = false;
        } else {
            city.setError(null);
        }

        if (upostcode.isEmpty() || upostcode.length() != 4){
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

        if(!upassword.equals(uconfirmPassword)){
            confirmPassword.setError("Passwords do not match");
            valid = false;
        }else {
            confirmPassword.setError(null);
        }

        return valid;
    }
}
