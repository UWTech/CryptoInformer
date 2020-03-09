package com.example.cryptoinformer.ui.crypto_prices;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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

import java.util.ArrayList;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

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

        stylizeLayout(textViews, prices, pricesLinearLayout);

        return root;
    }

    public void stylizeLayout(ArrayList<TextView> textViews, ArrayList<PriceRecord> priceRecords, LinearLayout targetLayout) {
        // get icons for currencies
        ArrayList<ImageView> icons = new ArrayList<>();

        for (PriceRecord priceRecord: priceRecords) {
            ImageView icon = generateCurrencyIconView(priceRecord);
            if (icon != null) {
                //icon.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                icons.add(icon);
            }
        }
        int iconIndex = 0;
        for (int i=0; i < textViews.size(); i +=2) {
            // TODO:: make sizing of icon pleasing
            // Element one: Currency icon at the head of the price info
            ImageView sizedIcon = icons.get(iconIndex);
            //sizedIcon.setScaleType(ImageView.ScaleType.MATRIX);
            targetLayout.addView(sizedIcon);
            //increment icon index
            iconIndex++;
            // Element two: price change in colored text
            // center text view
            TextView priceChangeTextView = textViews.get(i);
            priceChangeTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            targetLayout.addView(priceChangeTextView);
            // Element three: other currency metadata
            // center text view
            TextView currencyMetadataTextView = textViews.get(i+1);
            currencyMetadataTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            targetLayout.addView(currencyMetadataTextView);
        }
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

            String currentPrice = String.format("%.2f", new Float(cryptoPrice.price));

            String cryptoPriceString = String.format("%s \n%s \nPrice: $%s",
                    cryptoPrice.currSymbol, cryptoPrice.currName, currentPrice, cryptoPrice.logoURL);
            dynamicPriceViewElement.setText(cryptoPriceString + "\n\n\n");
            dynamicPriceViewElement.setTypeface(null, Typeface.BOLD);

            String priceChangeString = String.format("%.2f", priceChange);
            dynamicPriceChange.setText("$" + priceChangeString);
            dynamicPriceChange.setTypeface(null,Typeface.BOLD_ITALIC);

            cryptoViews.add(dynamicPriceChange);
            cryptoViews.add(dynamicPriceViewElement);
        }
        return cryptoViews;
    }

    public ImageView generateCurrencyIconView(PriceRecord priceRecord) {
        try {
            ImageView curencyIconView = new ImageView(App.context);
            GlideToVectorYou
                    .init()
                    .with(App.context)
                    .load(Uri.parse(priceRecord.logoURL), curencyIconView);
            return curencyIconView;
        }
        catch (Exception e){
            return null;
        }
    }


}