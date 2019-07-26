package com.itsp.attendance;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubjectFragment extends Fragment {
    private RecyclerView subjectRecycler;
    private SubjectAdapter subjectAdapter;
    private ArrayList<Subject> subjects;

    private JsonObjectRequest subjectObjectRequest;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO(Morne): Do something about screen rotation. Rotating the screen causes view to go
        // away within the fragment. Probably due to onCreate() in MainActivity being called again.

        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        RequestQueue queue = Volley.newRequestQueue(context);


        subjectObjectRequest = new JsonObjectRequest

                (Request.Method.POST, Config.url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

                                subject.setCode(subjectJSON.getString("code"));
                                subject.setAttendance(subjectJSON.getString("attendance"));
                                subject.setTotal(subjectJSON.getString("total"));
                                subject.setThumbnail("placeholder");

                                subjects.add(subject);

                            }

                            subjectAdapter.updateData(subjects);
                            subjectAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO(Morne): Loading symbol?
                        if (context != null)
                            Toast.makeText(context, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                    }
                });

        subjects = new ArrayList<>();

        subjectAdapter = new SubjectAdapter(subjects);

        subjectRecycler = view.findViewById(R.id.subject_recycler);
        subjectRecycler.setLayoutManager(new LinearLayoutManager(context));
        subjectRecycler.setItemAnimator(new DefaultItemAnimator());
        subjectRecycler.setAdapter(subjectAdapter);

        queue.add(subjectObjectRequest);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        context.getApplicationContext();
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

}
