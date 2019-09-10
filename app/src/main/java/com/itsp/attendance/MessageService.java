package com.itsp.attendance;

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

public class MessageService extends Service {
    
    String api_path = "/api/private";
    ArrayList<Notification> notifications;
    final static String TAG = "MessageService";
    JsonObjectRequest notificationObjectRequest;
    
    public MessageService(Context applicationContext) {
        super();
        
    }
    
    public MessageService() {
        
        notificationObjectRequest = new JsonObjectRequest(Request.Method.POST, Config.url + api_path, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    notifications = new ArrayList<>();
                    
                    JSONArray notificationArray = response.getJSONArray("notifications");
                    for(int i = 0; i < notificationArray.length(); i++) {
                        JSONObject notificationJSON = notificationArray.getJSONObject(i);
                        Notification notification = new Notification();
                        
                        notification.setTitle(notificationJSON.getString("title"));
                        notification.setDescription(notificationJSON.getString("description"));
                        notification.setIcon(notificationJSON.getString("icon"));
                        
                        notifications.add(notification);
                    }
                    
                    int notificationCount = 0;
                    for(Notification notification : notifications) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID).setSmallIcon(R.drawable.logo_fg).setContentTitle(notification.title).setContentText(notification.description).setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        
                        // NOTE(Morne): We must create a unique id for each notification to ensure that each one is displayed
                        String date = new SimpleDateFormat("ddHHmmss", Locale.UK).format(new Date());
                        date += notificationCount;
                        int id = Integer.parseInt(date);
                        notificationManager.notify(id, builder.build());
                        Log.d(TAG, "Notification was created");
                        
                        notificationCount++;
                    }
                    
                } catch(JSONException e) {
                    System.out.println(response);
//                    Log.e(TAG, "onResponse: Failed to parse JSON: ", e);
                }
            }
        }, new Response.ErrorListener() {
            
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO(Morne): Loading symbol?
                Log.e(TAG, "onErrorResponse: Failed to connect to server: " + error.getMessage());
                if(error.networkResponse != null && error.networkResponse.data != null) {
                    String s = new String(error.networkResponse.data);
                    Log.e(TAG, "onErrorResponse data: " + s);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + Config.accessToken);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        
        
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        new MessageWebSocket().run();

        // startTimer(); Timer for the POST request
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, MessageRestarterBroadcastReceiver.class);
        
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }
    
    private Timer timer;
    private TimerTask timerTask;
    
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        
        //schedule the timer, to wake up every 1 second
//        timer.schedule(timerTask, 2000, 2000); //
    }
    
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("MessageService:", " Running");
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(notificationObjectRequest);
                
                /*Test code for notifications */
                try {
                    notifications = new ArrayList<>();
                    
                    JSONObject json = new JSONObject();
                    JSONArray array = new JSONArray();
                    JSONObject item = new JSONObject();
                    item.put("title", "ITOO231");
                    item.put("description", "Your attendance has been noted!");
                    item.put("icon", "1d");
                    array.put(item);
                    
                    json.put("notifications", array);
                    
                    
                    JSONObject object = new JSONObject();
                    JSONArray notificationArray = json.getJSONArray("notifications");
                    for(int i = 0; i < notificationArray.length(); i++) {
                        JSONObject notificationJSON = notificationArray.getJSONObject(i);
                        Notification notification = new Notification();
                        
                        notification.setTitle(notificationJSON.getString("title"));
                        notification.setDescription(notificationJSON.getString("description"));
                        notification.setIcon(notificationJSON.getString("icon"));
                        
                        notifications.add(notification);
                    }
                    
                    int notificationCount = 0;
                    for(Notification notification : notifications) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), MainActivity.CHANNEL_ID).setSmallIcon(R.drawable.logo_fg).setContentTitle(notification.title).setContentText(notification.description).setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        
                        // NOTE(Morne): We must create a unique id for each notification to ensure that each one is displayed
                        String date = new SimpleDateFormat("ddHHmmss", Locale.UK).format(new Date());
                        date += notificationCount;
                        int id = Integer.parseInt(date);
                        notificationManager.notify(id, builder.build());
                        Log.d(TAG, "Notification was created");
                        
                        notificationCount++;
                    }
                    
                } catch(JSONException e) {
                    Log.e(TAG, "onResponse: Failed to parse JSON: ", e);
                }
                
                /**/
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
