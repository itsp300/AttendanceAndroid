package com.itsp.attendance.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PermanentService extends Service {
    private final static String TAG = PermanentService.class.getName();
    NotificationSocket notificationSocket;

    public PermanentService(Context applicationContext) {
        super();

    }

    public PermanentService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        notificationSocket = new NotificationSocket(getApplicationContext());

        //startTimer(); //Timer for the POST request
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, PermanentRestartReceiver.class);

        sendBroadcast(broadcastIntent);
        if(notificationSocket.socket != null)
        {
            notificationSocket.closeSocket();
        }
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 2000, 2000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.e(TAG, "run: " + "Service running!");
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

