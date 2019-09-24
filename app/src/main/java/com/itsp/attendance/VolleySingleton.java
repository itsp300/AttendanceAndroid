package com.itsp.attendance;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton
{
    private static final String TAG = com.itsp.attendance.VolleySingleton.class.getName();

    private static com.itsp.attendance.VolleySingleton instance;
    private RequestQueue requestQueue;

    private static Context context;

    private VolleySingleton(Context context)
    {
        this.context = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized com.itsp.attendance.VolleySingleton getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new com.itsp.attendance.VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        Log.d(TAG, "Added request: " + req.getUrl());
        getRequestQueue().add(req);
    }


}
