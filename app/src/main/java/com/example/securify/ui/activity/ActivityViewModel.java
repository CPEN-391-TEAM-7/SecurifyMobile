package com.example.securify.ui.activity;

import android.widget.ToggleButton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActivityViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ActivityViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is activity fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}