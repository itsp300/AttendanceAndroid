package com.itsp.attendance.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itsp.attendance.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
{

    private HomeViewModel homeViewModel;
    HomeAdapter homeAdapter;
    RecyclerView homeRecycler;
    List<Subject> subjects;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel =
                ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.context = getContext();
        homeViewModel.activity = getActivity();

        subjects = new ArrayList<>(); // empty subjects list for initial view

        homeAdapter = new HomeAdapter(subjects);

        homeRecycler = root.findViewById(R.id.home_recycler);
        homeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        homeRecycler.setItemAnimator(new DefaultItemAnimator());
        homeRecycler.setAdapter(homeAdapter);

        homeViewModel.getData().observe(this, new Observer<List<Subject>>()
        {
            @Override
            public void onChanged(@Nullable List<Subject> newData)
            {
                homeAdapter.updateData(newData);
                homeAdapter.notifyDataSetChanged();
            }
        });

        homeViewModel.loadData();

        return root;
    }
}