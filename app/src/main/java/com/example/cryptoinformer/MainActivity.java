package com.example.cryptoinformer;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
        //app = new App();
    }

    // onclick wrapper for price search button
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
        ArrayList<TextView> textViews = cryptoPricesFragment.generateTextViewRecords(priceRecords, pricesLinearLayout, App.getAppContext());

        int childCount = pricesLinearLayout.getChildCount();
        // index that price elements start at
        int priceIndexStart = 1;

        // delete the price elements not pertaining to the search query
        // size of list shrinks, so we start at the max index, and
        // subtract one until we reach the set index of static view elements
        for (int i = childCount - 1; i > priceIndexStart; i--) {
            pricesLinearLayout.removeViewAt(i);
        }
        // start adding new elements after the static elements in the view
        //for (TextView dynamicPriceViewElement : textViews) {
            cryptoPricesFragment.stylize_layout(textViews, priceRecords, pricesLinearLayout);
            //pricesLinearLayout.addView(dynamicPriceViewElement, priceIndexStart + 1);
        //}
        currencySymbolView.setText("");

        fragmentTransaction.attach(cryptoPricesFragment);

        // make keyboard disappear: https://stackoverflow.com/questions/4841228/after-type-in-edittext-how-to-make-keyboard-disappear
        InputMethodManager mgr = (InputMethodManager) getSystemService(App.getAppContext().INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(currencySymbolView.getWindowToken(), 0);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
