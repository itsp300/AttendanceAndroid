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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManager;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.VoidCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itsp.attendance.background.PermanentService;
import com.itsp.attendance.barcodereader.BarcodeCaptureActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
    Overall TODO list:
    - Test more devices to ensure CredentialManager is reliable across all of them.
    - Floating button may block content, fix.

 */

public class MainActivity extends AppCompatActivity
{
    final static String TAG = MainActivity.class.getName();
    final static int AUTH0_ADDITIONAL_CHECK = 1;
    private final static int RC_BARCODE_CAPTURE = 9001;
    public final static String CHANNEL_ID = "attendanceNotification";
    Auth0 auth0;
    SecureCredentialsManager credentialsManager;
    CredentialsManager compatCredentialsManager;
    FloatingActionButton qrReaderButton;
    Barcode barcode;

    PermanentService permanentService;
    Intent permanentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // NOTE(Morne): Navigation setup
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        qrReaderButton = findViewById(R.id.main_floating_scan);
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

        // NOTE(Morne): Config setup
        Config.url = Utility.loadRawResourceKey(this, R.raw.config, "url");
        Config.urlSocket = Utility.loadRawResourceKey(this, R.raw.config, "urlSocket");
        Utility.ASSERT(Config.url != null);
        Utility.ASSERT(Config.urlSocket != null);

        // NOTE(Morne): Authentication setup
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);

        permanentService = new PermanentService(this);
        permanentIntent = new Intent(this, permanentService.getClass());

        // TODO(Morne): Double check this if is fine with lower versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
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

        //getCredentials();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO(Morne): Barcode scanner requires newer versions of google play services, check if older devices like android 4.0 can easily obtain this.

        if (requestCode == RC_BARCODE_CAPTURE)
        {
            if (resultCode == CommonStatusCodes.SUCCESS)
            {
                if (data != null)
                {
                    barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (barcode != null)
                    {
                        Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Barcode read: " + barcode.displayValue);

                        Map<String, String> postParam = new HashMap<String, String>();
                        postParam.put("qrCode", barcode.displayValue);

                        String apiPath = "/api/secure/scan_code";
                        JsonObjectRequest qrObjectRequest = VolleyUtility.makeJsonObjectRequest(this, getApplicationContext(), TAG, Config.url + apiPath, new JSONObject(postParam),
                                new VolleyUtility.VolleyResponseListener()
                                {
                                    @Override
                                    public void onResponse(JSONObject response)
                                    {

                                        Log.d(TAG, "onResponse: " + response.toString());


                                        Log.d(TAG, "onResponse: QR code was sent!");


                                    }
                                });

                        VolleySingleton.getInstance(this).addToRequestQueue(qrObjectRequest);

                    }
                    else
                    {
                        Log.d(TAG, "Barcode was null.");
                    }
                }
                else
                {
                    Log.d(TAG, "No barcode captured, intent data is null.");
                }
            }
            else
            {
                Log.d(TAG, "onActivityResult: " + CommonStatusCodes.getStatusCodeString(resultCode));
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(permanentIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    public void getCredentials()
    {
        // NOTE(Morne): Support for the encryption used is limited to ap levels greater than 20
        if (Build.VERSION.SDK_INT >= 21)
        {
            // NOTE(Morne): Encryption allowed

            // TODO(Morne): Maybe not create this on every call.
            credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

            // TODO(Morne): The below authentication step breaks on non emulated phone.
            //credentialsManager.requireAuthentication(this, AUTH0_ADDITIONAL_CHECK, "User Authentication", null);

            credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>()
            {
                @Override
                public void onSuccess(final Credentials credentials)
                {
                    Config.accessToken = credentials.getAccessToken();
                    Utility.ASSERT(Config.accessToken != null);
                    Log.d(TAG, "Auth0 getCredentials onSuccess: token->  " + Config.accessToken);

                    if (!isMyServiceRunning(permanentService.getClass()))
                    {
                        startService(permanentIntent);
                    }

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Utility.debugToast(MainActivity.this, "Loaded credentials from storage");
                        }
                    });
                }

                @Override
                public void onFailure(CredentialsManagerException error)
                {
                    Log.e(TAG, "Auth0 getCredentials onFailure: " + error.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Utility.debugToast(MainActivity.this, "Failed to load credentials from storage");
                        }
                    });

                    login();
                }
            });
        }
        else
        {
            // NOTE(Morne): Encryption not allowed

            // TODO(Morne): Maybe not create this on every call.
            AuthenticationAPIClient apiClient = new AuthenticationAPIClient(auth0);
            compatCredentialsManager = new CredentialsManager(apiClient, new SharedPreferencesStorage(this));
            compatCredentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>()
            {
                @Override
                public void onSuccess(final Credentials credentials)
                {
                    Config.accessToken = credentials.getAccessToken();
                    Utility.ASSERT(Config.accessToken != null);
                    Log.d(TAG, "Auth0 getCredentials onSuccess: token->  " + Config.accessToken);

                    if (!isMyServiceRunning(permanentService.getClass()))
                    {
                        startService(permanentIntent);
                    }

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Utility.debugToast(MainActivity.this, "Loaded credentials from storage");
                        }
                    });

                }

                @Override
                public void onFailure(CredentialsManagerException error)
                {
                    Log.e(TAG, "Auth0 getCredentials onFailure: " + error.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Utility.debugToast(MainActivity.this, "Failed to load credentials from storage");
                        }
                    });

                    login();
                }
            });
        }
    }

    public void saveCredentials(Credentials credentials)
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            credentialsManager.saveCredentials(credentials);
        }
        else
        {
            compatCredentialsManager.saveCredentials(credentials);
        }
    }

    public void clearCredentials()
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            credentialsManager.clearCredentials();
        }
        else
        {
            compatCredentialsManager.clearCredentials();
        }
    }

    public boolean hasValidCredentials()
    {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 21)
        {
            if (credentialsManager.hasValidCredentials())
            {
                result = true;
            }

        }
        else
        {
            if (compatCredentialsManager.hasValidCredentials())
            {
                result = true;
            }
        }

        return result;
    }

    public void logout()
    {
        WebAuthProvider.logout(auth0).withScheme("demo").start(MainActivity.this, new VoidCallback()
        {
            @Override
            public void onFailure(Auth0Exception error)
            {
                Log.e(TAG, "Auth0 onFailure: " + error.getMessage());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Utility.debugToast(MainActivity.this, "Failed to logout");
                    }
                });
            }

            @Override
            public void onSuccess(Void payload)
            {
                clearCredentials();
                if (!hasValidCredentials())
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Utility.debugToast(MainActivity.this, "Cleared credentials storage");
                        }
                    });
                }
                Config.accessToken = Utility.loadRawResourceKey(MainActivity.this, R.raw.config, "invalidAccessToken");
                Utility.ASSERT(Config.accessToken != null);
                Log.d(TAG, "Auth0 logout onSuccess: true");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Utility.debugToast(MainActivity.this, "Logged out successfully");
                    }
                });

            }
        });
    }

    public void login()
    {
        WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withAudience("backend.itsp300.com")
                .withScope("openid offline_access")
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
                        Log.e(TAG, "Auth0 login onFailure: " + exception.getMessage());
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                Utility.debugToast(MainActivity.this, "Failed login");
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials)
                    {
                        Config.accessToken = credentials.getAccessToken();
                        Utility.ASSERT(Config.accessToken != null);
                        Log.d(TAG, "Auth0 login onSuccess: Token-> " + credentials.getAccessToken());
                        saveCredentials(credentials);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Utility.debugToast(MainActivity.this, "Saved credentials to storage");
                            }
                        });

                        if (hasValidCredentials())
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Utility.debugToast(MainActivity.this, "Logged in successfully");
                                }
                            });
                        }
                    }
                });
    }
}














