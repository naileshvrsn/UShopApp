package com.ushop.ushopapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectStoreActivity extends AppCompatActivity {

    private CardView countdownCard, paknsaveCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Store");

//        try{
//            actionBar.setDisplayHomeAsUpEnabled(true);
//
//        } catch (Exception e){
//
//        }

        countdownCard = findViewById(R.id.countdownCardSelectStore);
        paknsaveCard = findViewById(R.id.paknsaveCardSelectStore);

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
