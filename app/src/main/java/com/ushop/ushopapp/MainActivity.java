package com.ushop.ushopapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText _email, _password;
    private TextView _register, _resetPassword;
    private Button _login,_test;
    private ImageView infoButton;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // User is logged in
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            MainActivity.this.finish();
        }



        _email = findViewById(R.id.input_loginEmail);
        _password = findViewById(R.id.input_loginPassword);
        _login = findViewById(R.id.btn_login);
        _register = findViewById(R.id.link_register);
        _resetPassword = findViewById(R.id.link_reset_password);
        progressBar = findViewById(R.id.loginprogressbar);
        infoButton = findViewById(R.id.info_button_home);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
            }
        });

        _register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        _resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class).putExtra("email", _email.getText().toString()));
            }
        });

        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = _email.getText().toString();
                String password = _password.getText().toString();

                if(!validate()){
                    return;
                }
                else {
                    _password.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);

                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Login failed. Have you registered?", Toast.LENGTH_SHORT).show();
                                _password.setEnabled(true);
                            }
                            else {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }
                        }
                    });
                }
            }
        });


    }


    public boolean validate() {
        boolean valid = true;

        String email = _email.getText().toString();
        String password = _password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("Enter a valid email address");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (password.isEmpty()) {
            _password.setError("Enter password");
            valid = false;
        } else {
            _password.setError(null);
        }

        return valid;
    }



}
