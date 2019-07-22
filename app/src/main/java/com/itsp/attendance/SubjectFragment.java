package com.itsp.attendance;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class SubjectFragment extends Fragment {
    private RecyclerView subjectRecycler;
    private SubjectAdapter subjectAdapter;
    private ArrayList<Subject> subjects;

    public static final String[] subjectThumbnails = {"placeholder", "placeholder", "placeholder", "placeholder", "placeholder"};
    public static final String[] subjectCodes = {"ITOO211", "ITDA123", "ITCC111", "ITAC100", "ITEF199"};
    public static final String[] subjectAttendances = {"12", "31", "41", "1", "41"};
    public static final String[] subjecttotals = {"12", "34", "56", "1", "43"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO(Morne): Do something about screen rotation. Rotating the screen causes view to go
        // away within the fragment. Probably due to onCreate() in MainActivity being called again.

        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        // NOTE(Morne): 10.10.2.2 is a special host loop back address that only works with an emulator
        String url ="http://10.0.2.2:1984/resource";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(getTag(), response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);

        subjects = new ArrayList<>();
        for(int i=0;i<subjectCodes.length;i++)
        {
            Subject subject = new Subject();
            subject.setCode(subjectCodes[i]);
            subject.setAttendance(subjectAttendances[i]);
            subject.setTotal(subjecttotals[i]);
            subject.setThumbnail(subjectThumbnails[i]);

            subjects.add(subject);
        }

        subjectAdapter = new SubjectAdapter(subjects);

        subjectRecycler = view.findViewById(R.id.subject_recycler);
        subjectRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        subjectRecycler.setItemAnimator(new DefaultItemAnimator());
        subjectRecycler.setAdapter(subjectAdapter);

        return view;
    }
}
