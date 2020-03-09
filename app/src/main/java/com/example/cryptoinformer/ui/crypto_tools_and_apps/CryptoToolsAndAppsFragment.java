package com.example.cryptoinformer.ui.crypto_tools_and_apps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cryptoinformer.App;
import com.example.cryptoinformer.R;
import com.example.cryptoinformer.ui.crypto_prices.price_feed.PriceRecord;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata.AppMetadataRetriever;
import com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata.AppRecord;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.net.URL;
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

            // load icon into view
            ImageView iconImageView = generateAppViewIcon(appRecord.iconUri);
            toolsAndAppsLinearLayout.addView(iconImageView);

            // generate download link
            TextView dynamicDownloadLink = new TextView(getContext());
            dynamicDownloadLink.append(Html.fromHtml("<a href='" + appRecord.appUri + "'> Install </a>" +"\n" ));
            dynamicDownloadLink.setClickable(true);
            dynamicDownloadLink.setMovementMethod(LinkMovementMethod.getInstance());
            dynamicDownloadLink.setTypeface(null, Typeface.ITALIC);
            dynamicDownloadLink.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            toolsAndAppsLinearLayout.addView(dynamicDownloadLink);

            // generate metadata for app
            TextView dynamicAppToolMetadata = new TextView(getContext());
            String appMetadataString = String.format("Name: %s \n%s \nRating: %.1f\n\n",
                    appRecord.appName, appRecord.description, appRecord.rating);
            dynamicAppToolMetadata.setText(appMetadataString + "\n");
            dynamicAppToolMetadata.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dynamicAppToolMetadata.setTypeface(null, Typeface.BOLD);
            toolsAndAppsLinearLayout.addView(dynamicAppToolMetadata);

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

    public ImageView generateAppViewIcon(String iconURI) {
        try {
            ImageView appIconView = new ImageView(App.context);
            URL imageUrl = new URL(iconURI);
            Bitmap appIconImage = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            appIconView.setImageBitmap(appIconImage);
            return appIconView;
        }
        catch (Exception e){
            return null;
        }
    }
}