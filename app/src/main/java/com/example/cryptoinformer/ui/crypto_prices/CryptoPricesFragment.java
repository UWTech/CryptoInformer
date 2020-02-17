package com.example.cryptoinformer.ui.crypto_prices;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cryptoinformer.R;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.CryptoPriceGenerator;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.PriceRecord;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class CryptoPricesFragment extends Fragment {

    private CryptoPricesViewModel cryptoPricesViewModel;
    private CryptoPriceGenerator priceRetriever;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        priceRetriever = new CryptoPriceGenerator();

        cryptoPricesViewModel =
                ViewModelProviders.of(this).get(CryptoPricesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prices, container, false);
        LinearLayout pricesLinearLayout = (LinearLayout) root.findViewById(R.id.price_linear_layout);

        //TODO:: remove this after making class async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // retrieve prices
        ArrayList<PriceRecord> prices = priceRetriever.retrieveCryptoPrices();


        for (PriceRecord cryptoPrice: prices) {

            TextView dynamicPriceViewElement = new TextView(getContext());
            // TODO:: stylize elements, add graphic from logoURL
            String cryptoPriceString = String.format("Symbol: %s \nName: %s \nPrice: %s Price Change: %s \nLogo:%s",
                cryptoPrice.currSymbol, cryptoPrice.currName, cryptoPrice.price, cryptoPrice.priceChange, cryptoPrice.logoURL);
            dynamicPriceViewElement.setText(cryptoPriceString + "\n");
            pricesLinearLayout.addView(dynamicPriceViewElement);

        }

        final TextView textView = root.findViewById(R.id.prices);
        cryptoPricesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}