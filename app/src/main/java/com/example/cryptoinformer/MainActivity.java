package com.example.cryptoinformer;

import android.os.Bundle;
import android.view.View;

import com.example.cryptoinformer.ui.crypto_prices.price_feed.CryptoPriceGenerator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

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
    }

    // onclick wrapper for price search button
    public void searchForPrice(View view) {
        TextInputEditText currencySymbolView = findViewById(R.id.currency_search_text);
        String symbol = currencySymbolView.getText().toString();
        CryptoPriceGenerator.searchForPrice(symbol);
    }

}
