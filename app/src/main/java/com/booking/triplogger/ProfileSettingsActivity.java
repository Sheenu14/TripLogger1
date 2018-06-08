package com.booking.triplogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileSettingsActivity extends AppCompatActivity {

    @BindView(R.id.cancelBtn)
    AppCompatButton cancelBtn;

    @BindView(R.id.saveBtn)
    AppCompatButton saveBtn;

    @BindView(R.id.nameEditText)
    AppCompatEditText nameEditText;

    @BindView(R.id.idEditText)
    AppCompatEditText idEditText;

    @BindView(R.id.emailEditText)
    AppCompatEditText emailEditText;

    @BindView(R.id.commentsEditText)
    AppCompatEditText commentsEditText;

    @BindView(R.id.maleGender)
    AppCompatRadioButton maleGender;

    @BindView(R.id.femaleGender)
    AppCompatRadioButton femaleGender;

    DataBaseHelper dataBaseHelper;
    UserModelDetails userModelDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        ButterKnife.bind(this);

        dataBaseHelper = new DataBaseHelper(this);

        if (dataBaseHelper.getAllUsersDetails().size() > 0) {
            userModelDetails = dataBaseHelper.getAllUsersDetails().get(0);
            if (userModelDetails != null) {
                nameEditText.setText(userModelDetails.getName());
                idEditText.setText(userModelDetails.getUserID());
                emailEditText.setText(userModelDetails.getEmail());
                commentsEditText.setText(userModelDetails.getComment());

                if (userModelDetails.getGender().equals("Male")) {
                    maleGender.setChecked(true);
                } else {
                    femaleGender.setChecked(true);
                }
            }
        } else {
            nameEditText.setText("John");
            idEditText.setText("12345");
            emailEditText.setText("abc@gmail.com");
            commentsEditText.setText("Comments");
            maleGender.setChecked(true);
        }


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String id = idEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String comments = commentsEditText.getText().toString().trim();

                String gender = (maleGender.isChecked()) ? "Male" : "";

                if (gender.equals("")) {
                    gender = (femaleGender.isChecked()) ? "Female" : "";
                }

                if (name.length() <= 0 || id.length() <= 0 || email.length() <= 0 || comments.length() <= 0 || gender.length() <= 0) {
                    Toast.makeText(ProfileSettingsActivity.this, "Please enter the all fields", Toast.LENGTH_LONG).show();
                } else {
                    if (userModelDetails != null) {
                        dataBaseHelper.updateUserDetails(Integer.parseInt(userModelDetails.getId()), name, email, gender, comments, id);
                        Toast.makeText(ProfileSettingsActivity.this, "User field updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        dataBaseHelper.insertUserDetails(name, email, gender, comments, id);
                        Toast.makeText(ProfileSettingsActivity.this, "User field inserted successfully.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileSettingsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
