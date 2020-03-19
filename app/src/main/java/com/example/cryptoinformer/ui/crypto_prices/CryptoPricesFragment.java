package com.example.cryptoinformer.ui.crypto_prices;

import android.app.Activity;
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
import java.util.Arrays;
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
    public ArrayList<PriceRecord> curentDisplayedPrices;
    public View root;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        priceRetriever = new CryptoPriceGenerator();

        cryptoPricesViewModel =
                ViewModelProviders.of(this).get(CryptoPricesViewModel.class);
        root = inflater.inflate(R.layout.fragment_prices, container, false);
        LinearLayout pricesLinearLayout = (LinearLayout) root.findViewById(R.id.price_linear_layout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        
        StrictMode.setThreadPolicy(policy);
        // retrieve prices
        // check for saved state
        ArrayList<String> cryptoSymbolArrList = null;
        if (viewCryptoSymbols != null && !viewCryptoSymbols.isEmpty()) {
            String[] cryptoStringArr = viewCryptoSymbols.split(",");
            cryptoSymbolArrList = new ArrayList<>();
            for (String symbol : cryptoStringArr) {
                if (!symbol.isEmpty())
                    cryptoSymbolArrList.add(symbol);
            }
        }
        ArrayList<PriceRecord> prices = priceRetriever.retrieveCryptoPrices(cryptoSymbolArrList, this.priceChangeInterval);

        ArrayList<TextView> textViews = generateTextViewRecords(prices, pricesLinearLayout, App.getAppContext());

        stylizeLayout(textViews, prices, pricesLinearLayout, this.priceChangeInterval, getActivity());

        return root;
    }

    public void stylizeLayout(ArrayList<TextView> textViews, ArrayList<PriceRecord> priceRecords, LinearLayout targetLayout, String interval,
                              Activity activity) {
        // set the fragment's currently displayed Price Record objects in array list to facilitate saving in SharedPreferences
        curentDisplayedPrices = priceRecords;
        viewCryptoSymbols = generateSymbolString(priceRecords);
        priceChangeInterval = interval;

        // get icons for currencies
        ArrayList<ImageView> icons = new ArrayList<>();

        for (PriceRecord priceRecord: priceRecords) {
            ImageView icon = generateCurrencyIconView(priceRecord);
            if (icon != null) {
                icons.add(icon);
            }
        }
        int iconIndex = 0;
        for (int i=0; i < textViews.size(); i +=3) {
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
            // Element three: currency symbol string
            // center text view
            TextView currencySymbolView = textViews.get(i+1);
            currencySymbolView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            targetLayout.addView(currencySymbolView);
            // Element three: other currency metadata
            // center text view
            TextView currencyMetadataTextView = textViews.get(i+2);
            currencyMetadataTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            targetLayout.addView(currencyMetadataTextView);
        }
        storeCurrentViewState(viewCryptoSymbols, priceChangeInterval, activity);
    }

    public ArrayList<TextView> generateTextViewRecords(ArrayList<PriceRecord> priceRecords, LinearLayout pricesLinearLayout, Context context) {
        ArrayList<TextView> cryptoViews = new ArrayList<>();
        for (PriceRecord cryptoPrice: priceRecords) {
            TextView dynamicPriceViewElement = new TextView(context);
            TextView dynamicPriceChange = new TextView(context);
            TextView dynamicCurrencySymbol = new TextView(context);

            Float priceChange = new Float(cryptoPrice.priceChange);

            // determine price change color
            if (priceChange > 0) {
                dynamicPriceChange.setTextColor(Color.GREEN);
            } else {
                dynamicPriceChange.setTextColor(Color.RED);
            }

            String currentPrice = String.format("%.2f", new Float(cryptoPrice.price));

            String currencySymbol = String.format("%s", cryptoPrice.currSymbol);
            dynamicCurrencySymbol.setText(currencySymbol);
            dynamicCurrencySymbol.setTypeface(null, Typeface.BOLD);

            String cryptoPriceString = String.format("%s \nPrice: $%s",
                    cryptoPrice.currName, currentPrice, cryptoPrice.logoURL);
            dynamicPriceViewElement.setText(cryptoPriceString + "\n\n\n");
            dynamicPriceViewElement.setTypeface(null, Typeface.BOLD);

            String priceChangeString = String.format("%.2f", priceChange);
            dynamicPriceChange.setText("$" + priceChangeString);
            dynamicPriceChange.setTypeface(null,Typeface.BOLD_ITALIC);

            cryptoViews.add(dynamicPriceChange);
            cryptoViews.add(dynamicCurrencySymbol);
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

        // retrieve the currency symbols from the saved view to restore the previous view state
        if (sharedPref.contains(CRYPTO_SYMBOLS.toString())) {
            this.viewCryptoSymbols = sharedPref.getString(CRYPTO_SYMBOLS.toString(),null);
        }
        if (sharedPref.contains(PRICE_CHANGE_INTERVAL.toString())) {
            String priceChangeDayCount = sharedPref.getString(PRICE_CHANGE_INTERVAL.toString(), "1");
            this.priceChangeInterval = priceChangeDayCount + "d";
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

    public void storeCurrentViewState (String cryptoSymbols, String interval, Activity activity) {
        /**
         * method responsible for storing relevant
         * variables in shared preferences for
         * later retrieval to restore view state
         */
        // use shared preferences rather than savedInstancestate, due to
        // know Android bug with fragment recreation when used as bottom nav
        // elements

        // if not activity in this view context, return without storing, and rely on on pause
        if (activity == null){
            return;
        }

        // set the mode of the preferences to private to ensure user security
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        // retrieve the current View's crypto symbols, and store in shared preferences
        editor.putString(CRYPTO_SYMBOLS.toString(), cryptoSymbols);
        // remove the trailing day value if present
        if (interval.contains("d")) {
            interval = interval.substring(0, (interval.length() -1));
        }
        //store the numeric day count of the price change interval
        editor.putString(PRICE_CHANGE_INTERVAL.toString(), interval);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
        /**
         * In all other non-create cases,
         * onResume can be guaranteed to be called prior to the activity starting
         * So we first set the variables to whatever was stored in
         * shared preferences, and then restore the view to what it
         * was prior to it being destroyed
         */
        // check if the variables are default, or if they have already been restored
        // from the saved state. This indicates, we do not need to restore the view here
        Boolean isDefault = areDefaultVars();
        if (isDefault) {
            // take no action; the default view
            // is auto generated on create, so no need to restore state
        } else {
            // restore view, keeping in mind prices may have changed since last refresh of view,
            // so we need to retrieve the prices again

            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String interval = sharedPref.getString(PRICE_CHANGE_INTERVAL.toString(),"1");
            interval += "d";
            String cryptoSymoblsString =  sharedPref.getString(CRYPTO_SYMBOLS.toString(),null);

            // if empty string was saved, or the string is null,
            // rely on the default view creation behavior
            if (cryptoSymoblsString != null && !cryptoSymoblsString.isEmpty()) {
                LinearLayout priceLinearLayoutView = (LinearLayout) root.findViewById(R.id.price_linear_layout);

                ArrayList<String> cryptoSymbolStringArr = new ArrayList(Arrays.asList(cryptoSymoblsString.split(",")));

                ArrayList<PriceRecord> priceRecords = priceRetriever.retrieveCryptoPrices(cryptoSymbolStringArr, interval);
                ArrayList<TextView> priceTextViews = generateTextViewRecords(priceRecords, priceLinearLayoutView, getContext());
                stylizeLayout(priceTextViews, priceRecords, priceLinearLayoutView, interval, getActivity());
            }
        }
    }

    public boolean areDefaultVars() {
        /**
         * method that determines if the variables associated with the view are
         * in their default state, or have already been loaded from shared preferences
         */
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String interval = sharedPref.getString(PRICE_CHANGE_INTERVAL.toString(),"1");
        interval += "d";
        String cryptoSymoblsString =  sharedPref.getString(CRYPTO_SYMBOLS.toString(),null);
        if ((this.priceChangeInterval.equals(this.DEFAULT_INTERVAL) && this.viewCryptoSymbols == null) ||
                (this.priceChangeInterval.equals(interval) && this.viewCryptoSymbols.equals(cryptoSymoblsString))){
            return true;
        }// else
        return false;
    }

    @Override
    public void onPause() {
        // only need to override on pause,
        // as this is always called before onStop and
        // onDestroy
        super.onPause();

        LinearLayout priceLinearLayoutView = (LinearLayout) root.findViewById(R.id.price_linear_layout);
        Spinner intervalSpinner = priceLinearLayoutView.getRootView().findViewById(R.id.interval_selector_list);
        // retrieve the currently selected price change interval
        String interval = (String) intervalSpinner.getSelectedItem();

        // get the total number of elements in the current search results
        int childCount = priceLinearLayoutView.getChildCount();
        // index that price elements start at
        int priceIndexStart = 4;
        // will hold the comma separated currency symbols
        StringBuilder cryptoSymbolsBuilder = new StringBuilder();

        // iterate over the elements, and extract the string that contains the currency symbol
        for (int i = (childCount - 1); i > priceIndexStart; i -=4) {
            // each crypto currency view item consists of three distinct view elements
            // icon (image view), price change text view, crypto symbol text view, and metadata text view
            int symbolTextViewIndex = i - 1;
            TextView cryptoSymbolView = (TextView) priceLinearLayoutView.getChildAt(symbolTextViewIndex);
            String cryptoSymbol = cryptoSymbolView.getText().toString();
            cryptoSymbolsBuilder.append(cryptoSymbol);

            // decrement to indicate this cyrptocurrency's view is saved
            //i -= 4;
            // if this is not the last element, append a comma
            int nextCount = (i -4);
            if (nextCount > priceIndexStart) {
                cryptoSymbolsBuilder.append(",");
            }
        }
        String cryptoSymbols = cryptoSymbolsBuilder.toString();
        storeCurrentViewState(cryptoSymbols, interval, getActivity());
    }
}