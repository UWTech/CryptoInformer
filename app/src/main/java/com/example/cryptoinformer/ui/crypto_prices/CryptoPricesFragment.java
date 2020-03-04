package com.example.cryptoinformer.ui.crypto_prices;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.cryptoinformer.App;
import com.example.cryptoinformer.R;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.CryptoPriceGenerator;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.PriceRecord;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CryptoPricesFragment extends Fragment {

    private CryptoPricesViewModel cryptoPricesViewModel;
    private CryptoPriceGenerator priceRetriever;
    public View root;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        priceRetriever = new CryptoPriceGenerator();

        cryptoPricesViewModel =
                ViewModelProviders.of(this).get(CryptoPricesViewModel.class);
        root = inflater.inflate(R.layout.fragment_prices, container, false);
        LinearLayout pricesLinearLayout = (LinearLayout) root.findViewById(R.id.price_linear_layout);
        //TODO:: remove this after making class async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        
        StrictMode.setThreadPolicy(policy);
        // retrieve prices
        ArrayList<PriceRecord> prices = priceRetriever.retrieveCryptoPrices();

        ArrayList<TextView> textViews = generateTextViewRecords(prices, pricesLinearLayout, App.getAppContext());

        // get icons for currencies
        ArrayList<ImageView> icons = new ArrayList<>();

        for (PriceRecord priceRecord: prices) {
            ImageView icon = generateCurrencyIconView(priceRecord);
            if (icon != null) {
                icons.add(icon);
            }
        }

        int i = 0;

        for (TextView dynamicPriceViewElement : textViews) {
            if (i < icons.size()) {
                pricesLinearLayout.addView(icons.get(i));
            }
            pricesLinearLayout.addView(dynamicPriceViewElement);
            i++;
        }
        return root;
    }

    public ArrayList<TextView> generateTextViewRecords(ArrayList<PriceRecord> priceRecords, LinearLayout pricesLinearLayout, Context context) {
        ArrayList<TextView> cryptoViews = new ArrayList<>();
        for (PriceRecord cryptoPrice: priceRecords) {
            TextView dynamicPriceViewElement = new TextView(context);
            TextView dynamicPriceChange = new TextView(context);

            Float priceChange = new Float(cryptoPrice.priceChange);

            // determine price change color
            if (priceChange > 0) {
                dynamicPriceChange.setTextColor(Color.GREEN);
            } else {
                dynamicPriceChange.setTextColor(Color.RED);
            }
            String cryptoPriceString = String.format("%s \n%s \nPrice: %s  \nLogo:%s",
                    cryptoPrice.currSymbol, cryptoPrice.currName, cryptoPrice.price, cryptoPrice.logoURL);
            dynamicPriceViewElement.setText(cryptoPriceString + "\n");
            dynamicPriceViewElement.setTypeface(null, Typeface.BOLD);

            String priceChangeString = String.format("%s", cryptoPrice.priceChange);
            dynamicPriceChange.setText(priceChangeString);
            dynamicPriceChange.setTypeface(null,Typeface.BOLD_ITALIC);

            cryptoViews.add(dynamicPriceChange);
            cryptoViews.add(dynamicPriceViewElement);
        }
        return cryptoViews;
    }

    public ImageView generateCurrencyIconView(PriceRecord priceRecord) {

        try {
            // build the URL
            URL url = new URL(priceRecord.logoURL);
            //retrieve the Bitmnap
            Bitmap currencyIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            ImageView curencyIconView = new ImageView(App.context);
            curencyIconView.setImageBitmap(currencyIcon);
            return curencyIconView;
        }
        catch (Exception e){
            return null;
        }
    }


}