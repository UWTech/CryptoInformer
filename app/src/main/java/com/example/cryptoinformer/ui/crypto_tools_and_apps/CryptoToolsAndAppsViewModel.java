package com.example.cryptoinformer.ui.crypto_tools_and_apps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CryptoToolsAndAppsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CryptoToolsAndAppsViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}