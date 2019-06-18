package com.ushop.ushopapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    //link UI elements to class
    private EditText _nameText, _streetText, _suburbText, _cityText, _postcodeText, _emailText, _passwordText;
    private Button _signupButton;
    private TextView _loginLink;
    private FirebaseAuth mAuth;
    private ProgressBar PB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*PB = findViewById(R.id.progressbar);
        PB.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }

        _nameText = findViewById(R.id.input_name);
        _streetText = findViewById(R.id.input_name);
        _suburbText = findViewById(R.id.input_name);
        _cityText = findViewById(R.id.input_name);
        _postcodeText = findViewById(R.id.input_name);
        _emailText = findViewById(R.id.input_name);
        _passwordText = findViewById(R.id.input_name);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = _emailText.getText().toString().trim();
                final String password = _passwordText.getText().toString().trim();

                if (!validate()) {
                    onSignupFailed();
                    return;
                }
                else {
                    PB.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        PB.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                }

            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });*/
    }

    //public void onSignupFailed() {
      //  Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        //_signupButton.setEnabled(true);
    //}

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String street = _streetText.getText().toString();
        String suburb = _suburbText.getText().toString();
        String city = _cityText.getText().toString();
        String postcode = _postcodeText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 2) {
            _nameText.setError("at least 2 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (street.isEmpty() || street.length() < 5){
            _streetText.setError("at least 5 characters");
            valid = false;
        } else {
            _streetText.setError(null);
        }

        if (suburb.isEmpty() || suburb.length() < 4){
            _suburbText.setError("at least 4 characters");
            valid = false;
        } else {
            _suburbText.setError(null);
        }

        if (city.isEmpty() || city.length() < 6){
            _cityText.setError("at least 6 characters");
            valid = false;
        } else {
            _cityText.setError(null);
        }

        if (postcode.isEmpty() || postcode.length() != 4){
            _postcodeText.setError("enter a valid postcode");
            valid = false;
        } else {
            _postcodeText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;

    }

}
