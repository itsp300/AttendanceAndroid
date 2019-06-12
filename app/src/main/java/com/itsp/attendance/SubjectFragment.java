package com.itsp.attendance;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubjectFragment extends Fragment {
    RecyclerView subjectRecycler;
    SubjectAdapter subjectAdapter;
    ArrayList<Subject> subjects;

    public static final String[] subjectCodes = {"test", "test", "test", "test", "test"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subject, container, false);
        subjects = new ArrayList<>();

        for(int i=0;i<subjectCodes.length;i++)
        {
            Subject subject = new Subject();
            subject.setCode(subjectCodes[i]);
            subject.setAttendance(subjectCodes[i]);
            subject.setTotal(subjectCodes[i]);
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
