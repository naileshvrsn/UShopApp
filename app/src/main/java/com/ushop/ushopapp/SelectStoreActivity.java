package com.ushop.ushopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectStoreActivity extends AppCompatActivity {

    private TextView signOut, welcomeUser;
    private CardView countdownCard, paknsaveCard;
    private FirebaseAuth mAuth;
    private ImageView infoButton;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);
        getSupportActionBar().hide();

        //progress bar while activity loads

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("users").document(mAuth.getUid());

        signOut = findViewById(R.id.link_logout_selectStore);
        welcomeUser = findViewById(R.id.welcomeTextView);
        countdownCard = findViewById(R.id.countdownCardSelectStore);
        paknsaveCard = findViewById(R.id.paknsaveCardSelectStore);
        infoButton = findViewById(R.id.info_button_home);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                SelectStoreActivity.this.finish();
            }
        });

//      make this faster if possible
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        User currentUserFirestore = document.toObject(User.class);
                        welcomeUser.setText("Welcome " + currentUserFirestore.name);
                    }
                    else {

                    }
                }
                else {
                    Toast.makeText(SelectStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
            }
        });

        countdownCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownClicked();
            }
        });

        paknsaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paknsaveClicked();
            }
        });
    }


    private void countdownClicked(){
        startActivity(new Intent(getApplicationContext(), CategoryActivity.class).putExtra("store", "Countdown"));
    }

    private void paknsaveClicked(){
        startActivity(new Intent(getApplicationContext(), CategoryActivity.class).putExtra("store", "PaknSave"));
    }

}
