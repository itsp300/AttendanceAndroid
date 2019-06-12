package com.itsp.attendance;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    HomeFragment homeFragment;
    SubjectFragment subjectFragment;
    NotificationFragment notificationFragment;



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
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

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
    };

}
