package com.itsp.attendance.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itsp.attendance.Config;
import com.itsp.attendance.MainActivity;
import com.itsp.attendance.VolleySingleton;
import com.itsp.attendance.VolleyUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardViewModel extends ViewModel
{
    private final static String TAG = DashboardViewModel.class.getName();
    private MutableLiveData<DashboardData> data;
    public Activity activity;
    JsonObjectRequest jsonDataRequest;
    public Context context;

    LiveData<DashboardData> getData()
    {
        if (data == null)
        {
            data = new MutableLiveData<>();

            String apiPath = "/api/secure/summary";
            jsonDataRequest = VolleyUtility.makeJsonObjectRequest(activity, context, TAG, Config.url + apiPath,
                    new VolleyUtility.VolleyResponseListener()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                Log.d(TAG, "onResponse: " + response.toString());
                                int attendedTotal = response.getInt("attendanceTotal");
                                int classTotal = response.getInt("lectureTotal");
                                int missedTotal = 0;

                                missedTotal = classTotal - attendedTotal;

                                // TODO(Morne): Check to see if bulletproof. Not nulls etc
                                DashboardData data = new DashboardData();

                                data.studentName = response.getString("studentName");
                                data.attendedTotal = Integer.toString(attendedTotal);
                                data.classTotal = Integer.toString(classTotal);
                                data.missedTotal = Integer.toString(missedTotal);
                                data.ratingPercentage = (((float) attendedTotal / (float) classTotal) * 100f);

                                DashboardViewModel.this.data.postValue(data);

                                Log.d(TAG, "onResponse: Dashboard data updated");

                            } catch (JSONException e)
                            {
                                Log.e(TAG, "onResponse: Failed to parse JSON: ", e);
                            }
                        }
                    });

            //loadData(); // <-- Runs on first creation and never again assuming its not called anywhere else
        }

        return data;
    }

    void loadData()
    {
        VolleySingleton.getInstance(context).addToRequestQueue(jsonDataRequest);
    }
}