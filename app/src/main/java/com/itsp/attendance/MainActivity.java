package com.itsp.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itsp.attendance.barcodereader.BarcodeCaptureActivity;

public class MainActivity extends AppCompatActivity {
    HomeFragment homeFragment;
    SubjectFragment subjectFragment;
    NotificationFragment notificationFragment;

    FloatingActionButton qrReaderButton;

    private static final int RC_BARCODE_CAPTURE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        subjectFragment = new SubjectFragment();
        notificationFragment = new NotificationFragment();

        // NOTE(Morne): Sets the initial fragment to the home_fragment.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.navigation_subject:
                        fragmentTransaction.replace(R.id.fragment_container, subjectFragment);
                        fragmentTransaction.commit();
                        return true;
                    case R.id.navigation_notification:
                        fragmentTransaction.replace(R.id.fragment_container, notificationFragment);
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        });

        qrReaderButton = findViewById(R.id.qr_reader_button);
        qrReaderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }});

    }


}
