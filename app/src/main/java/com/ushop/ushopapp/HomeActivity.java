package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeUser;
    private ImageView homeImage, logoutIcon, shopIcon, viewOrdersIcon, editProfileIcon, addProductIcon, updateProductIcon;
    private TableLayout adminTableLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    private User currentUserFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        //progress bar while activity loads
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("users").document(mAuth.getUid());

        welcomeUser = findViewById(R.id.welcomeTextView);
        homeImage = findViewById(R.id.home_image);
        shopIcon = findViewById(R.id.homeShopIcon);
        viewOrdersIcon = findViewById(R.id.homeViewOrdersIcon);
        editProfileIcon = findViewById(R.id.homeEditProfileIcon);
        logoutIcon = findViewById(R.id.homeLogoutIcon);
        adminTableLayout = findViewById(R.id.adminTableLayoutHome);
        addProductIcon = findViewById(R.id.homeNewProductIcon);
        updateProductIcon = findViewById(R.id.homeUpdateProductIcon);

        if(mAuth.getCurrentUser() != null){
            String email = mAuth.getCurrentUser().getEmail();
            if(email.equals("naileshvrsn@gmail.com")){
                adminTableLayout.setVisibility(View.VISIBLE);
            }
//            if(email.equals("svekaria124@gmail.com")){
//                adminTableLayout.setVisibility(View.VISIBLE);
//            }
        }

        addProductIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddProductActivity.class));
            }
        });

        shopIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SelectStoreActivity.class));
            }
        });

        editProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
            }
        });

        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                HomeActivity.this.finish();
            }
        });

//      Get user object of logged in user from firestore. make this faster if possible
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        currentUserFirestore = document.toObject(User.class);
                        welcomeUser.setText("Welcome " + currentUserFirestore.getName());
                    }
                    else {
                        //could not get user from firestore database hence back to login screen
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        HomeActivity.this.finish();
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        pDialog.dismissWithAnimation();
    }



}
