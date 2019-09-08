package com.itsp.attendance;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment
{
    private static final String TAG = NotificationFragment.class.getName();
    private Context context;

    private RecyclerView notificationRecycler;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notifications;

    private JsonObjectRequest notificationObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // TODO(Morne): Do something about screen rotation. Rotating the screen causes view to go
        // away within the fragment. Our switchFragment() deletes the fragment causing onCreateView
        // to be called again

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        String api_path = "/notificationFragment";
        notificationObjectRequest = new JsonObjectRequest
                (Request.Method.POST, "http://10.0.2.2:5000" , null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            notifications = new ArrayList<>();

                            JSONArray notificationArray = response.getJSONArray("Notifications");
                            for (int i = 0; i < notificationArray.length(); i++) {
                                JSONObject notificationJSON = notificationArray.getJSONObject(i);
                                Notification notification = new Notification();

                                notification.setTitle(notificationJSON.getString("title"));
                                notification.setDescription(notificationJSON.getString("description"));

                                notifications.add(notification);

                            }

                            notificationAdapter.updateData(notifications);
                            notificationAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onResponse: Notification data updated");

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: Failed to parse JSON: ", e);
                        }
                    }
                }, new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // TODO(Morne): Loading symbol?
                        if (context != null) {
                            Toast.makeText(context, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                        Log.e(TAG, "onErrorResponse: Failed to connect to server: " + error.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+ Config.idToken);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        notifications = new ArrayList<>();

        notificationAdapter = new NotificationAdapter(notifications);

        notificationRecycler = view.findViewById(R.id.notification_recycler);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(context));
        notificationRecycler.setItemAnimator(new DefaultItemAnimator());
        notificationRecycler.setAdapter(notificationAdapter);

        VolleySingleton.getInstance(context).addToRequestQueue(notificationObjectRequest);


        Notification notification = new Notification();
        notification.setTitle("Testing1");
        notification.setDescription("This is my new test message. This message needs to be long enough to ensure that the padding is correct");

        notifications.add(notification);

        notification.setTitle("Testing2");
        notification.setDescription("This is my new test message. This message needs to be long enough to ensure that the padding is correct");

        notifications.add(notification);

        notificationAdapter.updateData(notifications);
        notificationAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        context = context.getApplicationContext();
        this.context = context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        context = null;
    }
}