package com.itsp.attendance.dep;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itsp.attendance.*;


public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getName();

    EditText studentNumber_editText;
    EditText password_editText;
    ProgressBar login_progress;
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        studentNumber_editText = findViewById(R.id.studentNumber_editText);
        password_editText = findViewById(R.id.password_editText);
        login_progress = findViewById(R.id.login_progress);
        login_button = findViewById(R.id.login_button);

        // TODO(Morne): Check to see if the user is already authenticated and skip login

        login_button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO(Morne): Actually do the login. Will most likely need to use Async Class so it can
                // do the tasks in the background???
                login_progress.setVisibility(View.VISIBLE);

                boolean loginValid = true;
                login_progress.setVisibility(View.INVISIBLE);
                if (loginValid)
                {
                    Log.d(TAG, "Valid login.");
                    // TODO(Morne): Pass through login status and type(lecturer/student)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                {
                    Log.d(TAG, "Invalid login.");
                    Toast.makeText(LoginActivity.this, "Invalid login.\nPlease try again...", Toast.LENGTH_SHORT).show();
                    // error message
                }
            }
        });
    }


}