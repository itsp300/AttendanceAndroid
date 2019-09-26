package com.itsp.attendance.ui.home;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.toolbox.JsonObjectRequest;
import com.itsp.attendance.BuildConfig;
import com.itsp.attendance.Config;
import com.itsp.attendance.MainActivity;
import com.itsp.attendance.Utility;
import com.itsp.attendance.VolleySingleton;
import com.itsp.attendance.VolleyUtility;
import com.itsp.attendance.ui.dashboard.DashboardData;
import com.itsp.attendance.ui.dashboard.DashboardViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel
{

    private final static String TAG = DashboardViewModel.class.getName();
    private MutableLiveData<List<Subject>> data;
    public Activity activity;
    JsonObjectRequest jsonDataRequest;
    public Context context;

    LiveData<List<Subject>> getData()
    {
        if (data == null)
        {
            data = new MutableLiveData<>();

            String apiPath = "/api/secure/subjectAttendances";
            jsonDataRequest = VolleyUtility.makeJsonObjectRequest(activity, context, TAG, Config.url + apiPath, null,
                    new VolleyUtility.VolleyResponseListener()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                Log.d(TAG, "onResponse: " + response.toString());
                                List<Subject> subjects = new ArrayList<>();

                                JSONArray subjectArray = response.getJSONArray("subjectAttendances");
                                for (int i = 0; i < subjectArray.length(); i++) {
                                    JSONObject subjectJSON = subjectArray.getJSONObject(i);
                                    Subject subject = new Subject();

                                    subject.setCode(subjectJSON.getString("subjectCode"));
                                    subject.setAttendance(subjectJSON.getString("attendanceTotal"));
                                    subject.setTotal(subjectJSON.getString("lectureTotal"));
                                    subject.setThumbnail(subjectJSON.getString("imageName"));
                                    subjects.add(subject);
                                }

                                data.postValue(subjects);

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