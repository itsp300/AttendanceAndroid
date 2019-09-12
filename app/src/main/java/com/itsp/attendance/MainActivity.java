package com.itsp.attendance;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itsp.attendance.barcodereader.BarcodeCaptureActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/* TODO LIST:
    - Check to see if QR code text is not displayed when scanned.
    - Check android version compatibility.
*/
public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();
    private static final int RC_BARCODE_CAPTURE = 9001;
    final static String CHANNEL_ID = "attendanceNotification";

    Auth0 auth0;

    Intent messageIntent;
    private MessageService messageService;

    HomeFragment homeFragment;
    SubjectFragment subjectFragment;
    NotificationFragment notificationFragment;

    FloatingActionButton qrReaderButton;
    Barcode barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        login();

        // TODO(Morne): Config read maybe should not exist in release build.
        Config.url = ResourceLoader.loadRawResourceKey(this, R.raw.config, "url");
        Config.urlSocket = ResourceLoader.loadRawResourceKey(this, R.raw.config, "urlSocket");

        //new MessageWebSocket().run();

        messageService = new MessageService(this);
        messageIntent = new Intent(this, messageService.getClass());
        if (!isMyServiceRunning(messageService.getClass())) {
            startService(messageIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getString(R.string.channel_name);
            String description = this.getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        homeFragment = new HomeFragment();
        subjectFragment = new SubjectFragment();
        notificationFragment = new NotificationFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                switch (item.getItemId()) {
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

    public void login()
    {
        WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withAudience("backend.itsp300.com")
                .start(MainActivity.this, new AuthCallback()
                {
                    @Override
                    public void onFailure(@NonNull final Dialog dialog)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials)
                    {
                        Config.accessToken = credentials.getAccessToken();
                        Log.d(TAG, "Access Token: " + credentials.getAccessToken());
                        // NOTE(Morne): Sets the initial fragment to the home_fragment.
                        switchFragment(homeFragment);
                    }
                });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    protected void onDestroy()
    {
        stopService(messageIntent);
        Log.i(TAG, "onDestroy!");
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Barcode result
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (barcode != null) {
                        Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Barcode read: " + barcode.displayValue);

                        Map<String, String> postParam = new HashMap<String, String>();
                        postParam.put("qrCode", barcode.displayValue);

                        String api_path = "/api/secure/scan_code";
                        JsonObjectRequest qrObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                Config.url + api_path, new JSONObject(postParam),
                                new Response.Listener<JSONObject>()
                                {
                                    @Override
                                    public void onResponse(JSONObject response)
                                    {
                                        // TODO(Morne): Response should not echo the value sent
                                        Log.d(TAG, "onResponse: " + response.toString());
                                    }
                                }, new Response.ErrorListener()
                        {

                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null) {
                                    String data = new String(networkResponse.data);
                                    Log.e(TAG, "onErrorResponse:\nbody:\n" + data);

                                    if (networkResponse.statusCode == 500 || networkResponse.statusCode == 401) {
                                        login();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Network error!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Network error!\nNo response received!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError
                            {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Authorization", "Bearer " + Config.accessToken);
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };

                        VolleySingleton.getInstance(this).addToRequestQueue(qrObjectRequest);

                    } else {
                        Log.d(TAG, "Barcode was null.");
                    }

                } else {
                    Log.d(TAG, "No barcode captured, intent data is null.");
                }
            } else {
                Log.d(TAG, "onActivityResult: " + CommonStatusCodes.getStatusCodeString(resultCode));
            }
        } else {
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
