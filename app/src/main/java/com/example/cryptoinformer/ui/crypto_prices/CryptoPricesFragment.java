package com.example.cryptoinformer.ui.crypto_prices;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.Comparator;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

public class CryptoPricesFragment extends Fragment {


    // lookup key used to retrieve saved symbols from last view
    // used during state management via lifecycle methods
    public static final Integer CRYPTO_SYMBOLS = 66;
    // last interval for price search
    public static final Integer PRICE_CHANGE_INTERVAL = 67;

    public static final String DEFAULT_INTERVAL = "1d";
    // holds the last known list of Crypto symbols
    // as a comma separated string
    public String viewCryptoSymbols = null;
    // holds last known interval for price change
    public String priceChangeInterval = "1d";


    private CryptoPricesViewModel cryptoPricesViewModel;
    private CryptoPriceGenerator priceRetriever;
    private ArrayList<PriceRecord> curentDisplayedPrices;
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
        // check for saved state
        ArrayList<String> cryptoSymbolArrList = null;
        if (viewCryptoSymbols != null) {
            String[] cryptoStringArr = viewCryptoSymbols.split(",");
            cryptoSymbolArrList = new ArrayList<>();
            for (String symbol : cryptoStringArr) {
                cryptoSymbolArrList.add(symbol);
            }
        }
        ArrayList<PriceRecord> prices = priceRetriever.retrieveCryptoPrices(cryptoSymbolArrList,this.priceChangeInterval);

        ArrayList<TextView> textViews = generateTextViewRecords(prices, pricesLinearLayout, App.getAppContext());

        stylizeLayout(textViews, prices, pricesLinearLayout, this.priceChangeInterval);

        return root;
    }

    public void stylizeLayout(ArrayList<TextView> textViews, ArrayList<PriceRecord> priceRecords, LinearLayout targetLayout, String interval) {
        // set the fragment's currently displayed Price Record objects in array list to facilitate saving in SharedPreferences
        this.curentDisplayedPrices = priceRecords;
        this.viewCryptoSymbols = generateSymbolString(priceRecords);
        this.priceChangeInterval = interval;

        // get icons for currencies
        ArrayList<ImageView> icons = new ArrayList<>();

        for (PriceRecord priceRecord: priceRecords) {
            ImageView icon = generateCurrencyIconView(priceRecord);
            if (icon != null) {
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

    public ArrayList<PriceRecord> sortCryptoPriceChangeLowToHigh(ArrayList<PriceRecord> priceRecords) {
        // rely on overriden comparator to return sort
        Collections.sort(priceRecords, new Comparator<PriceRecord>() {
            @Override
            public int compare(PriceRecord lhPriceRecords, PriceRecord rhPriceRecord) {
                return Double.compare(new Double(lhPriceRecords.priceChange), new Double(rhPriceRecord.priceChange));
            }
        });
        return priceRecords;    }

    public ArrayList<PriceRecord> sortCryptoPriceChangeHightoLow(ArrayList<PriceRecord> priceRecords) {
        // rely on overriden comparator to return sort
        Collections.sort(priceRecords, new Comparator<PriceRecord>() {
            @Override
            public int compare(PriceRecord lhPriceRecords, PriceRecord rhPriceRecord) {
                return Double.compare(new Double(rhPriceRecord.priceChange), new Double(lhPriceRecords.priceChange));
            }
        });
        return priceRecords;
    }

    public ArrayList<PriceRecord> sortCryptoAlphabetically(ArrayList<PriceRecord> priceRecords) {
        // rely on overriden comparator to return sort
        Collections.sort(priceRecords, new Comparator<PriceRecord>() {
            @Override
            public int compare(PriceRecord lhPrice, PriceRecord rhPrice) {
                int compareResult = lhPrice.currName.compareToIgnoreCase(rhPrice.currName);
                return compareResult;
            }
        });
        return priceRecords;
    }

    public void setVarsForViewCreation() {
        /**
         * method responsible for retrieving variables from
         * shared preferences if they were stored.
         * If not variables under the keys are saved, do nothing, and
         * let default class settings render view
         */
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String viewCurrencySymbols = null;
        String viewPriceChangeInterval = null;

        // retrieve the currency symbols from the saved view to restore the previous view state
        if (sharedPref.contains(CRYPTO_SYMBOLS.toString())) {
            this.viewCryptoSymbols = sharedPref.getString(CRYPTO_SYMBOLS.toString(),null);
        }
        if (sharedPref.contains(PRICE_CHANGE_INTERVAL.toString())) {
            this.priceChangeInterval = sharedPref.getString(PRICE_CHANGE_INTERVAL.toString(), null);
        }
    }

    public String generateSymbolString(ArrayList<PriceRecord> priceRecords) {
        // iterate over the price objects, get the symbols, and create a
        // comma separated string for storage in shared preferences that will be used to restore the view
        StringBuilder symbolsStringBuilder = new StringBuilder();
        int numRecords = priceRecords.size();
        for (int i = 0; i < numRecords; i++)
        {
            PriceRecord priceRecord = priceRecords.get(i);
            String cryptoSymbol = priceRecord.currSymbol;
            symbolsStringBuilder.append(cryptoSymbol);
            // don't add comma if this is the last record
            if (i < numRecords - 1) {
                symbolsStringBuilder.append(",");
            }
        }
        String cryptoSymbols = symbolsStringBuilder.toString();
        return cryptoSymbols;
    }

    public void storeCurrentViewState (String cryptoSymbols, String interval) {
        /**
         * method responsible for storing relevant
         * variables in shared preferences for
         * later retrieval to restore view state
         */
        // use shared preferences rather than savedInstancestate, due to
        // know Android bug with fragment recreation when used as bottom nav
        // elements

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // retrieve the current View's crypto symbols, and store in shared preferences
        editor.putString(CRYPTO_SYMBOLS.toString(), cryptoSymbols);
        // save the current interval used for price change
        editor.putString(PRICE_CHANGE_INTERVAL.toString(), interval + "d");
        editor.commit();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        /**
         this is the initial call to create the fragment,
         we simply need to set the variables, and
         let onCreateView run next to generate the view
        */
        setVarsForViewCreation();
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * In all other non-create cases,
         * onResume can be guaranteed to be called prior to the activity starting
         * So we first set the variables to whatever was stored in
         * shared preferences, and then restore the view to what it
         * was prior to it being destroyed
         */
        // check if there were saved variables in shared preferences
        // this indicates that there was a persisted state, and that
        // we do need to restore the view
        Boolean isDefault = areDefaultVars();
        if (isDefault) {
            String test = null;
        } else {
            // TODO :: replace view with saved variables
            String test = null;
        }
        // do nothing, the view is the default. No need to recreate
    }

    public boolean areDefaultVars() {
        /**
         * method that determines if the variables associated with the view are
         * in their default state
         */
        if (this.priceChangeInterval == this.DEFAULT_INTERVAL && this.viewCryptoSymbols == null) {
            return true;
        }// else
        return false;
    }

    @Override
    public void onPause() {
        // only need to override on stop,
        // as this is always called before onStop and
        // onDestroy
        super.onPause();

        LinearLayout priceLinearLayoutView = (LinearLayout) root.findViewById(R.id.price_linear_layout);
        Spinner intervalSpinner = priceLinearLayoutView.getRootView().findViewById(R.id.interval_selector_list);
        // retrieve the currently selected price change interval
        String interval = (String) intervalSpinner.getSelectedItem();

        // get a comma separated string from the array of price list names
        String cryptoSymbols = generateSymbolString(this.curentDisplayedPrices);
        storeCurrentViewState(cryptoSymbols, interval);
    }
}