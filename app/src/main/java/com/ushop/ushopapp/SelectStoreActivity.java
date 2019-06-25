package com.ushop.ushopapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SelectStoreActivity extends AppCompatActivity {

    private CardView countdownCard, paknsaveCard;
    private FloatingActionButton cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Store");

        countdownCard = findViewById(R.id.countdownCardSelectStore);
        paknsaveCard = findViewById(R.id.paknsaveCardSelectStore);
        cartBtn = findViewById(R.id.cartViewbtnSelectStore);

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

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CartListActivity.class));
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
