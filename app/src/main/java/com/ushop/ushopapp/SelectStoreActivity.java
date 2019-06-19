package com.ushop.ushopapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SelectStoreActivity extends AppCompatActivity {

    private TextView signOut, welcomeUser;
    private CardView countdownCard, paknsaveCard;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        signOut = findViewById(R.id.link_logout_selectStore);
        welcomeUser = findViewById(R.id.welcomeTextView);
        countdownCard = findViewById(R.id.countdownCardSelectStore);
        paknsaveCard = findViewById(R.id.paknsaveCardSelectStore);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            String userWelcome = "Welcome " + currentUser.getEmail();
            welcomeUser.setText(userWelcome);
        }

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
