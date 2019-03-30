package com.itsp.attendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText studentNumber_editText;
    EditText password_editText;
    ProgressBar login_progress;
    Button login_button;
    TextView register_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        studentNumber_editText = findViewById(R.id.studentNumber_editText);
        password_editText = findViewById(R.id.password_editText);
        login_progress = findViewById(R.id.login_progress);
        login_button = findViewById(R.id.login_button);
        register_text = findViewById(R.id.register_text);

        // TODO(Morne): Check to see if the user is already authenticated and skip login

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // TODO(Morne): Actually do the login. Will most likely need to use Async Class so it can
                // do the tasks in the background???
                login_progress.setVisibility(View.VISIBLE);

                // Check login

                login_progress.setVisibility(View.INVISIBLE);

                // See if successful
                boolean login_valid = true;
                if(login_valid) {
                    // NOTE(Morne): The intent is empty as we should not need to come back to
                    // this activity, could possible change.
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    // error message
                }
            }});

        register_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // TODO(Morne): Actually register the user
                Toast.makeText(LoginActivity.this, "Register text Works", Toast.LENGTH_SHORT).show();
            }});
    }
}