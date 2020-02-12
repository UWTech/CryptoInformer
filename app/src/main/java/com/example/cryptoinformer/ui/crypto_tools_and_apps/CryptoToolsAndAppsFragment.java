package com.example.cryptoinformer.ui.crypto_tools_and_apps;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cryptoinformer.R;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.PriceRecord;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata.AppMetadataRetriever;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata.AppRecord;

import java.util.ArrayList;

public class CryptoToolsAndAppsFragment extends Fragment {

    private CryptoToolsAndAppsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(CryptoToolsAndAppsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools_and_apps, container, false);

        AppMetadataRetriever appMetadataRetriever = new AppMetadataRetriever();

        LinearLayout toolsAndAppsLinearLayout = (LinearLayout) root.findViewById(R.id.tools_and_apps_linear_layout);

        //TODO:: remove this after making class async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // retrieve prices
        ArrayList<AppRecord> apps = appMetadataRetriever.getAppMetadata();

        for (AppRecord appRecord: apps) {

            TextView dynamicPriceViewElement = new TextView(getContext());
            // TODO:: stylize elements, add graphic from logoURL
            String appMetadataString = String.format("Name: %s \n Description: %s \n Icon URI: %s \nRating: %.1f",
                    appRecord.appName, appRecord.description, appRecord.iconUri, appRecord.rating);
            dynamicPriceViewElement.setText(appMetadataString + "\n");
            toolsAndAppsLinearLayout.addView(dynamicPriceViewElement);

        }

        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}