package com.itsp.attendance.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.itsp.attendance.BuildConfig;
import com.itsp.attendance.MainActivity;
import com.itsp.attendance.Utility;

public class HomeViewModel extends ViewModel
{

    private MutableLiveData<String> mText;

    public HomeViewModel()
    {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}