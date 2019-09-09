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
import com.android.volley.NetworkResponse;
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

public class SubjectFragment extends Fragment
{
    private static final String TAG = SubjectFragment.class.getName();
    private Context context;

    private RecyclerView subjectRecycler;
    private SubjectAdapter subjectAdapter;
    private ArrayList<Subject> subjects;

    private JsonObjectRequest subjectObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // TODO(Morne): Do something about screen rotation. Rotating the screen causes view to go
        // away within the fragment. Our switchFragment() deletes the fragment causing onCreateView
        // to be called again

        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        String api_path = "/api/secure/subjectAttendances";
        subjectObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Config.url + api_path, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            subjects = new ArrayList<>();

                            /* NOTE(Morne): Example of the incoming JSON
                                {
                                  "subjectAttendances": [
                                    {
                                      "code": "IT00311",
                                      "attendance": 3,
                                      "total": 4
                                    },
                                    {
                                      "code": "ITDA211",
                                      "attendance": 1,
                                      "total": 5
                                    }
                                  ]
                                }
                             */

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

                            subjectAdapter.updateData(subjects);
                            subjectAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onResponse: Subject data updated");

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: Failed to parse JSON: ", e);
                        }
                    }
                }, new Response.ErrorListener()
                {

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

                                if(networkResponse.statusCode == 500 || networkResponse.statusCode == 401)
                                {
                                    ((MainActivity)getActivity()).login();
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

        subjects = new ArrayList<>();

        subjectAdapter = new SubjectAdapter(subjects);

        subjectRecycler = view.findViewById(R.id.subject_recycler);
        subjectRecycler.setLayoutManager(new LinearLayoutManager(context));
        subjectRecycler.setItemAnimator(new DefaultItemAnimator());
        subjectRecycler.setAdapter(subjectAdapter);

        VolleySingleton.getInstance(context).addToRequestQueue(subjectObjectRequest);

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
