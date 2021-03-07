package com.example.securify.ui.blacklist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BlackListViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    public BlackListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Blacklist");
    }

    public LiveData<String> getText() {
        return mText;
    }
}