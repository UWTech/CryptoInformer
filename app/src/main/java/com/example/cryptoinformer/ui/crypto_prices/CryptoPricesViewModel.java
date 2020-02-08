package com.example.cryptoinformer.ui.crypto_prices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CryptoPricesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CryptoPricesViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}