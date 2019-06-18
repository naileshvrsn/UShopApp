package com.ushop.ushopapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText _email, _password;
    private TextView _register;
    private Button _login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        _email = findViewById(R.id.input_email);
        _password = findViewById(R.id.input_name);
        _login = findViewById(R.id.btn_login);
        _register = findViewById(R.id.link_register);

        //_register.setOnClickListener(new View.OnClickListener(){
          //  @Override
            //public void onClick(View v) {
                //or startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
              //  startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            //}
        //});



    }

}
