package com.ushop.ushopapp;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CategoryActivity extends AppCompatActivity {

    private ImageView storeLogo;
    private RelativeLayout bakeryLayout, confectioneryLayout, freshProduceLayout, frozenLayout, drinksLayout, winesAndBeerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        String selectedStore = getIntent().getExtras().getString("store");
        getSupportActionBar().setTitle(selectedStore);

        storeLogo = findViewById(R.id.store_image_category);
        bakeryLayout = findViewById(R.id.categoriesBakeryLayout);
        confectioneryLayout = findViewById(R.id.categoriesConfectioneryLayout);
        freshProduceLayout = findViewById(R.id.categoriesFreshProduceLayout);
        frozenLayout = findViewById(R.id.categoriesFrozenLayout);
        drinksLayout = findViewById(R.id.categoriesDrinksLayout);
        winesAndBeerLayout = findViewById(R.id.categoriesWinesAndBeerLayout);

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


    }
}
