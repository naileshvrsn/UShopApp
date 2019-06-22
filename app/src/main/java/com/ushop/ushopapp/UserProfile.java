package com.ushop.ushopapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName, cancel;
    private EditText email, dob, street, suburb, city, postcode;
    private Button saveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("Edit Profile");

        userImage = findViewById(R.id.editProfileImageView);
        userName = findViewById(R.id.userProfileName);
        cancel = findViewById(R.id.userProfileCancelButton);
        email = findViewById(R.id.userProfileEmail);
        dob = findViewById(R.id.userProfileDateOfBirth);
        street = findViewById(R.id.userProfileStreet);
        suburb = findViewById(R.id.userProfileSuburb);
        city = findViewById(R.id.userProfileCity);
        postcode = findViewById(R.id.userProfilePostCode);
        saveChanges = findViewById(R.id.userProfileSaveChangesButton);



    }
}
