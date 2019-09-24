package com.itsp.attendance;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyUtility
{
    public interface VolleyResponseListener {

        void onResponse(JSONObject response);
    }

    public static JsonObjectRequest makeJsonObjectRequest(final Activity activity, final Context context, final String TAG, String url, final VolleyResponseListener listener)
    {
        return new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener()
                {
                    // TODO(Morne): Nice error messages
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse networkResponse = error.networkResponse;
                        if(context != null)
                        {
                            if(networkResponse != null)
                            {
                                String data = new String(networkResponse.data);
                                Log.e(TAG, "onErrorResponse:\nbody:\n" + data);

                                // TODO(Morne): Test error codes in 500'ish (Malformed token error)
                                if(networkResponse.statusCode == 401)
                                {
                                    ((MainActivity)activity).getCredentials();
                                }
                                else
                                {
                                    Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(context, "Network error!\nNo response received!", Toast.LENGTH_SHORT).show();
                            }
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
    }
}
