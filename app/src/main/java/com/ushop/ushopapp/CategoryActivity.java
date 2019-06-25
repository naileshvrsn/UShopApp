package com.ushop.ushopapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryActivity extends AppCompatActivity {

    private ImageView storeLogo;
    private RelativeLayout bakeryLayout, confectioneryLayout, freshProduceLayout, frozenLayout, drinksLayout, winesAndBeerLayout;
    private FloatingActionButton cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        final String selectedStore = getIntent().getExtras().getString("store");
        getSupportActionBar().setTitle("Shop from " + selectedStore);

        storeLogo = findViewById(R.id.store_image_category);
        bakeryLayout = findViewById(R.id.categoriesBakeryLayout);
        confectioneryLayout = findViewById(R.id.categoriesConfectioneryLayout);
        freshProduceLayout = findViewById(R.id.categoriesFreshProduceLayout);
        frozenLayout = findViewById(R.id.categoriesFrozenLayout);
        drinksLayout = findViewById(R.id.categoriesDrinksLayout);
        winesAndBeerLayout = findViewById(R.id.categoriesWinesAndBeerLayout);
        cartBtn = findViewById(R.id.cartViewbtnCategories);

        final Bundle extras = new Bundle();
        extras.putString("store", selectedStore);

        switch (selectedStore){
            case "Countdown":
                storeLogo.setImageResource(R.drawable.countdown_logo);
                bakeryLayout.setBackgroundResource(R.drawable.rounded_button_green);
                confectioneryLayout.setBackgroundResource(R.drawable.rounded_button_green);
                freshProduceLayout.setBackgroundResource(R.drawable.rounded_button_green);
                frozenLayout.setBackgroundResource(R.drawable.rounded_button_green);
                drinksLayout.setBackgroundResource(R.drawable.rounded_button_green);
                winesAndBeerLayout.setBackgroundResource(R.drawable.rounded_button_green);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.countdownBrightGreen)));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.countdownGreen));
                }
                break;

            case "PaknSave" :
                storeLogo.setImageResource(R.drawable.paknsave_logo);
                bakeryLayout.setBackgroundResource(R.drawable.rounded_button_yellow);
                confectioneryLayout.setBackgroundResource(R.drawable.rounded_button_yellow);
                freshProduceLayout.setBackgroundResource(R.drawable.rounded_button_yellow);
                frozenLayout.setBackgroundResource(R.drawable.rounded_button_yellow);
                drinksLayout.setBackgroundResource(R.drawable.rounded_button_yellow);
                winesAndBeerLayout.setBackgroundResource(R.drawable.rounded_button_yellow);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.paknsaveBrightYellow)));
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(getResources().getColor(R.color.paknsaveYellow));
                }
                break;
            default:
        }


        bakeryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extras.putString("category", "Bakery");
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class).putExtras(extras));
            }
        });

        confectioneryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extras.putString("category", "Confectionery");
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class).putExtras(extras));
            }
        });

        freshProduceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extras.putString("category", "Fresh Produce");
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class).putExtras(extras));
            }
        });

        frozenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extras.putString("category", "Frozen");
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class).putExtras(extras));
            }
        });

        drinksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extras.putString("category", "Drinks");
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class).putExtras(extras));
            }
        });

        winesAndBeerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extras.putString("category", "Wines and Beer");
                startActivity(new Intent(getApplicationContext(), ProductListActivity.class).putExtras(extras));
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CartListActivity.class));
            }
        });

    }
}
