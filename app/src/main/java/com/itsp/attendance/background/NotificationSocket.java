package com.itsp.attendance.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.itsp.attendance.Config;
import com.itsp.attendance.MainActivity;
import com.itsp.attendance.R;
import com.itsp.attendance.database.DatabaseClient;
import com.itsp.attendance.database.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class NotificationSocket extends WebSocketListener
{
    private final static String TAG = "MessageWebSocket";
    static int PARTIAL_UNIQUE = 0;
    Context context;
    WebSocket socket;

    // TODO(Morne): Retry on fail
    NotificationSocket(Context context)
    {
        init(context);
    }

    NotificationSocket() {

    }

    public void init(Context context)
    {
        this.context = context;
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(Config.urlSocket)
                .build();
        WebSocket socket = client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256("ITSP300");
            String token = JWT.create().withClaim("type", "auth_mobile").withClaim("access_token", Config.accessToken).sign(algorithm);
            webSocket.send(token);

            Log.d(TAG, "onOpen: " + "Socket Token: " + token);
        } catch (JWTCreationException exception)
        {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String jwt)
    {

        Log.d(TAG, "onMessage: jwt-> " + jwt);

        String response = new String(Base64.decode(JWT.decode(jwt).getPayload(), Base64.DEFAULT));
        Log.d(TAG, "onMessage: notification-> " + response);

        try
        {
            JSONObject notificationJSON = new JSONObject(response);

            if(notificationJSON.getString("type").equals("notif_mobile"))
            {
                String title = notificationJSON.getString("title");
                String description = notificationJSON.getString("description");

                class SaveNotification extends AsyncTask<Void, Void, Void>
                {
                    String title;
                    String description;
                    SaveNotification(String title, String description)
                    {
                        this.title = title;
                        this.description = description;
                    }

                    @Override
                    protected Void doInBackground(Void... voids)
                    {
                        Log.d(TAG, "SaveNotification: Inserting notification to db.");
                        //creating a task
                        Notification notification = new Notification();
                        notification.setTitle(title);
                        notification.setDescription(description);

                        //adding to database
                        DatabaseClient.getInstance(context).getAppDatabase()
                                .notificationDao()
                                .insert(notification);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid)
                    {
                        super.onPostExecute(aVoid);
                        Log.d(TAG, "SaveNotification onPostExecute: Completed inserting notification.");
                    }
                }
                SaveNotification sn = new SaveNotification(title, description);
                sn.execute();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // NOTE(Morne): We must create a unique id for each notification to ensure that each one is displayed
                String date = new SimpleDateFormat("ddHHmmss", Locale.UK).format(new Date());
                int id = (int) (Integer.parseInt(date) + PARTIAL_UNIQUE);
                PARTIAL_UNIQUE++;
                notificationManager.notify(id, builder.build());
                Log.d(TAG, "Notification was created");
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes)
    {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason)
    {

        webSocket.close(1000, null);
        Log.d(TAG, "onClosing: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response)
    {
        t.printStackTrace();
    }
}
