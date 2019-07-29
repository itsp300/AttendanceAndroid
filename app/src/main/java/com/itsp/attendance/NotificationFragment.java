package com.itsp.attendance;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class NotificationFragment extends Fragment
{
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        context = null;
    }
}
