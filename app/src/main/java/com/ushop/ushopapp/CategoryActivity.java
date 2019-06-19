package com.ushop.ushopapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CategoryActivity extends AppCompatActivity {

    private ImageView storeLogo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String selectedStore = getIntent().getExtras().getString("store");
        getSupportActionBar().setTitle(selectedStore);

        mAuth = FirebaseAuth.getInstance();
        storeLogo = findViewById(R.id.store_image_category);

        switch (selectedStore){
            case "Countdown": storeLogo.setImageResource(R.drawable.countdown_logo); break;
            case "PaknSave" : storeLogo.setImageResource(R.drawable.paknsave_logo); break;
            default: storeLogo.setImageResource(R.drawable.ushop_text_only);
        }


    }
}
