package com.example.securify.ui.whitelist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WhiteListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WhiteListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Whitelist");
    }

    public LiveData<String> getText() {
        return mText;
    }
}