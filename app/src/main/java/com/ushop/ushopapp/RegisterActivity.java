package com.ushop.ushopapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ushop.ushopapp.Model.User;

import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    private EditText _nameText, _streetText, _suburbText, _postcodeText,_dateOfBirth, _phone, _emailText, _passwordText, _confirmPassword;
    private Button _signupButton;
    private TextView _loginLink;
    private DatePickerDialog datePickerDialog;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore firestoreDb;
    private Date selectedDateOfBirth;
    private int selectedYear;
    private String[] cities;
    private AppCompatAutoCompleteTextView _cityAutoText;

    private String defaultImageStorageLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        firestoreDb = FirebaseFirestore.getInstance();
        //defaultImageStorageLocation = "";
        defaultImageStorageLocation = "https://firebasestorage.googleapis.com/v0/b/ushop-73f4b.appspot.com/o/userImages%2Fblank_user.png?alt=media&token=ed4de9cf-befb-4fed-ad64-485f609ab709";

        cities = getResources().getStringArray(R.array.nz_cities_array);

        _nameText = findViewById(R.id.input_name);
        _streetText = findViewById(R.id.input_street);
        _suburbText = findViewById(R.id.input_suburb);
        _cityAutoText = findViewById(R.id.auto_input_city);
        _postcodeText = findViewById(R.id.input_postcode);
        _dateOfBirth = findViewById(R.id.input_dateofbirth);
        _phone = findViewById(R.id.input_phone);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _confirmPassword = findViewById(R.id.input_confirmPassword);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        _cityAutoText.setAdapter(new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, cities));
        _cityAutoText.setThreshold(1);

        _dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //date chosen
                        _dateOfBirth.setText((month + 1) + "/" + dayOfMonth + "/" + year);

                        //set variable chosenDate with the date from picker
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(0);
                        cal.set(year, month, dayOfMonth, 0, 0, 0);
                        selectedDateOfBirth = cal.getTime();
                        selectedYear = cal.get(Calendar.YEAR);

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = _emailText.getText().toString().trim();
                final String password = _passwordText.getText().toString().trim();
                String name = _nameText.getText().toString();
                String street = _streetText.getText().toString();
                String suburb = _suburbText.getText().toString();
                String city = _cityAutoText.getText().toString();
                String postcode = _postcodeText.getText().toString();
                String phone = _phone.getText().toString();

                if (!validate()) {
                    return;
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    final User user = new User();
                    user.setName(name);
                    user.setStreet(street);
                    user.setSuburb(suburb);
                    user.setCity(city);
                    user.setPostCode(postcode);
                    user.setPhone(phone);
                    user.setDateOfBirth(selectedDateOfBirth);
                    user.setUserImageLocation(defaultImageStorageLocation);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                            Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        firestoreDb.collection("users").document(mAuth.getUid()).set(user).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(!task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                else {
                                                    Toast.makeText(RegisterActivity.this, "Successful ", Toast.LENGTH_LONG).show();

                                                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if(task.isSuccessful()){
                                                                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(RegisterActivity.this, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        else {
                                                                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });

                                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    RegisterActivity.this.finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String street = _streetText.getText().toString();
        String suburb = _suburbText.getText().toString();
        String city = _cityAutoText.getText().toString();
        String postcode = _postcodeText.getText().toString();
        String dateOfBirth = _dateOfBirth.getText().toString();
        String phone = _phone.getText().toString();
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        String confirmPassword = _confirmPassword.getText().toString().trim();

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
            _cityAutoText.setError("at least 6 characters");
            valid = false;
        } else {
            _cityAutoText.setError(null);
        }

        if (postcode.isEmpty() || postcode.length() != 4){
            _postcodeText.setError("Enter a valid postcode");
            valid = false;
        } else {
            _postcodeText.setError(null);
        }

        if (dateOfBirth.isEmpty()){
            _dateOfBirth.setError("Select date of birth");
            valid = false;
        } else {
            _dateOfBirth.setError(null);
        }

        if(calculateAge() < 16 ){
            //_dateOfBirth.setError("You must be over the age of 16");
            Toast.makeText(RegisterActivity.this, "You must be over the age of 16", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            _dateOfBirth.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 8 || phone.length() > 12){
            _phone.setError("Enter a valid phone number");
            valid = false;
        }
        else {
            _phone.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if(confirmPassword.isEmpty() || !password.equals(confirmPassword)){
            _confirmPassword.setError("Passwords do not match");
            valid = false;
        }else {
            _confirmPassword.setError(null);
        }

        return valid;
    }


    private int calculateAge(){
        Calendar cal = Calendar.getInstance();
        int age = (cal.get(Calendar.YEAR)) - selectedYear;
        return age;
    }

}
