package com.itsp.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itsp.attendance.barcodereader.BarcodeCaptureActivity;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();
    private static final int RC_BARCODE_CAPTURE = 9001;

    HomeFragment homeFragment;
    SubjectFragment subjectFragment;
    NotificationFragment notificationFragment;

    FloatingActionButton qrReaderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        subjectFragment = new SubjectFragment();
        notificationFragment = new NotificationFragment();

        // NOTE(Morne): Sets the initial fragment to the home_fragment.
        switchFragment(homeFragment);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.navigation_home:
                        switchFragment(homeFragment);
                        return true;
                    case R.id.navigation_subject:
                        switchFragment(subjectFragment);
                        return true;
                    case R.id.navigation_notification:
                        switchFragment(notificationFragment);
                        return true;
                }
                return false;
            }
        });

        qrReaderButton = findViewById(R.id.qr_reader_button);
        qrReaderButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);

                // TODO(Morne): Decide whether the auto focus and flash should be used for QR scanning
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Barcode result
        if (requestCode == RC_BARCODE_CAPTURE)
        {
            if (resultCode == CommonStatusCodes.SUCCESS)
            {
                if (data != null)
                {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (barcode != null)
                    {
                        Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    } else
                    {
                        Log.d(TAG, "Barcode was null.");
                    }

                } else
                {
                    Log.d(TAG, "No barcode captured, intent data is null.");
                }
            } else
            {
                //statusMessage.setText(String.format(getString(R.string.barcode_error),
                // CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void switchFragment(Fragment fragment)
    {
        Log.d(TAG, "switchFragment: " + fragment);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }
}
