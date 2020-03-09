package com.example.cryptoinformer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cryptoinformer.ui.crypto_prices.CryptoPricesFragment;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.CryptoPriceGenerator;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.PriceRecord;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.CryptoToolsAndAppsFragment;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata.AppMetadataRetriever;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata.AppRecord;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private App app;
    private CryptoPriceGenerator cryptoPriceGenerator;
    private AppMetadataRetriever appMetadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_news, R.id.navigation_prices, R.id.navigation_apps_and_tools)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        cryptoPriceGenerator = new CryptoPriceGenerator();
        appMetadataRetriever = new AppMetadataRetriever();
    }

    // prices
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void searchForPrice(View view) {
        // current search target
        TextInputEditText currencySymbolView = view.getRootView().findViewById(R.id.currency_search_text);
        String symbol = currencySymbolView.getText().toString();
        // current search interval
        Spinner intervalSpinner = view.getRootView().findViewById(R.id.interval_selector_list);
        String interval = (String) intervalSpinner.getSelectedItem();

        ArrayList<PriceRecord> priceRecords = cryptoPriceGenerator.searchForPrice(symbol, interval + "d");
        LinearLayout pricesLinearLayout = (LinearLayout) view.getRootView().findViewById(R.id.price_linear_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // create new Fragment
        CryptoPricesFragment cryptoPricesFragment = new CryptoPricesFragment();

        refreshPriceView(priceRecords, pricesLinearLayout, cryptoPricesFragment);

        currencySymbolView.setText("");

        fragmentTransaction.attach(cryptoPricesFragment);

        // make keyboard disappear: https://stackoverflow.com/questions/4841228/after-type-in-edittext-how-to-make-keyboard-disappear
        InputMethodManager mgr = (InputMethodManager) getSystemService(App.getAppContext().INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(currencySymbolView.getWindowToken(), 0);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void refreshPriceView(ArrayList<PriceRecord> priceRecords, LinearLayout pricesLinearLayout, CryptoPricesFragment cryptoPricesFragment) {

        // create new Fragment
        ArrayList<TextView> textViews = cryptoPricesFragment.generateTextViewRecords(priceRecords, pricesLinearLayout, App.getAppContext());

        int childCount = pricesLinearLayout.getChildCount();
        // index that price elements start at
        int priceIndexStart = 4;

        // delete the price elements not pertaining to the search query
        // size of list shrinks, so we start at the max index, and
        // subtract one until we reach the set index of static view elements
        for (int i = childCount - 1; i > priceIndexStart; i--) {
            pricesLinearLayout.removeViewAt(i);
        }
        cryptoPricesFragment.stylizeLayout(textViews, priceRecords, pricesLinearLayout);
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sortCryptoAlphabetically(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // create new Fragment
        CryptoPricesFragment cryptoPricesFragment = new CryptoPricesFragment();
        ArrayList<PriceRecord> unsortedPrices = cryptoPriceGenerator.retrieveCryptoPrices();
        ArrayList<PriceRecord> priceRecords = cryptoPricesFragment.sortCryptoAlphabetically(unsortedPrices);
        LinearLayout pricesLinearLayout = (LinearLayout) view.getRootView().findViewById(R.id.price_linear_layout);

        refreshPriceView(priceRecords, pricesLinearLayout,  cryptoPricesFragment);
        fragmentTransaction.attach(cryptoPricesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sortCryptoPriceChangeLowToHigh(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // create new Fragment
        CryptoPricesFragment cryptoPricesFragment = new CryptoPricesFragment();
        ArrayList<PriceRecord> unsortedPrices = cryptoPriceGenerator.retrieveCryptoPrices();
        ArrayList<PriceRecord> priceRecords = cryptoPricesFragment.sortCryptoPriceChangeLowToHigh(unsortedPrices);
        LinearLayout pricesLinearLayout = (LinearLayout) view.getRootView().findViewById(R.id.price_linear_layout);

        refreshPriceView(priceRecords, pricesLinearLayout,  cryptoPricesFragment);
        fragmentTransaction.attach(cryptoPricesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sortCryptoPriceChangeHighToLow(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // create new Fragment
        CryptoPricesFragment cryptoPricesFragment = new CryptoPricesFragment();
        ArrayList<PriceRecord> unsortedPrices = cryptoPriceGenerator.retrieveCryptoPrices();
        ArrayList<PriceRecord> priceRecords = cryptoPricesFragment.sortCryptoPriceChangeHightoLow(unsortedPrices);
        LinearLayout pricesLinearLayout = (LinearLayout) view.getRootView().findViewById(R.id.price_linear_layout);

        refreshPriceView(priceRecords, pricesLinearLayout,  cryptoPricesFragment);
        fragmentTransaction.attach(cryptoPricesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // tools and apps
    public void sortAppsHighToLow(View view) {

        // sort app records to replace current view
        ArrayList<AppRecord> appRecords = appMetadataRetriever.getAppMetadata();
        ArrayList<AppRecord> sortedAppRecords = appMetadataRetriever.sortHighToLow(appRecords);
        replaceToolsAndAppsView(view, sortedAppRecords);
    }

    public void sortAppsLowToHigh(View view) {
        // sort app records to replace current view
        ArrayList<AppRecord> appRecords = appMetadataRetriever.getAppMetadata();
        ArrayList<AppRecord> sortedAppRecords = appMetadataRetriever.sortLowToHigh(appRecords);
        replaceToolsAndAppsView(view, sortedAppRecords);
    }

    public void sortAppsAlphabetically(View view) {
        // sort app records to replace current view
        ArrayList<AppRecord> appRecords = appMetadataRetriever.getAppMetadata();
        ArrayList<AppRecord> sortedAppRecords = appMetadataRetriever.sortAlphabetically(appRecords);
        replaceToolsAndAppsView(view, sortedAppRecords);
    }

    public void replaceToolsAndAppsView(View view, ArrayList<AppRecord> sortedAppRecords) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // fresh fragment for tools and apps view
        CryptoToolsAndAppsFragment sortedFragment = new CryptoToolsAndAppsFragment();
        LinearLayout currentAppsAndToolsLayout = (LinearLayout) view.getRootView().findViewById(R.id.tools_and_apps_linear_layout);

        int childCount = currentAppsAndToolsLayout.getChildCount();
        // index that tools elements start at
        int toolMetadataIndexStart = 3;

        // delete the tools and apps elements, in order to replace with sorted list
        for (int i = childCount - 1; i > toolMetadataIndexStart; i--) {
            currentAppsAndToolsLayout.removeViewAt(i);
        }

        // add new elements to the linear layout
        sortedFragment.generateAndStylizeView(sortedAppRecords, currentAppsAndToolsLayout);

        // attach the now sorted fragment
        fragmentTransaction.attach(sortedFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
