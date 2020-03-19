package com.example.cryptoinformer.ui.crypto_tools_and_apps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.HashMap;

public class CryptoToolsAndAppsFragment extends Fragment {

    private static final Integer APP_SORT_ORDER = 75;

    private CryptoToolsAndAppsViewModel notificationsViewModel;
    // current sort order of apps
    private ArrayList<AppRecord> currentApps = null;

    // comma separated string that stores the last sort order of
    // apps
    private String appNameOrder = null;
    public View root;
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
        ArrayList<AppRecord> apps;
        // retrieve prices
        // if the state was not restored by onCreate
        if (currentApps == null) {
            // retrieve a fresh list of apps
            apps = appMetadataRetriever.getAppMetadata();
        } else {
            apps = currentApps;
        }

        generateAndStylizeView(apps, toolsAndAppsLinearLayout, getActivity());

        // store the current sort order in preferences

        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        this.root = root;
        return root;
    }

    public void generateAndStylizeView(ArrayList<AppRecord> appRecords, LinearLayout toolsAndAppsLinearLayout, Activity activity) {
        // store current order of apps
        storeCurrentState(appRecords, activity);
        currentApps = appRecords;

        for (AppRecord appRecord: appRecords) {

            // load icon into view
            ImageView iconImageView = generateAppViewIcon(appRecord.iconUri);
            toolsAndAppsLinearLayout.addView(iconImageView);

            // generate download link
            TextView dynamicDownloadLink = new TextView(App.context);
            dynamicDownloadLink.append(Html.fromHtml("<a href='" + appRecord.appUri + "'> Install </a>" +"\n" ));
            dynamicDownloadLink.setClickable(true);
            dynamicDownloadLink.setMovementMethod(LinkMovementMethod.getInstance());
            dynamicDownloadLink.setTypeface(null, Typeface.ITALIC);
            dynamicDownloadLink.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            toolsAndAppsLinearLayout.addView(dynamicDownloadLink);

            // generate metadata for app
            TextView dynamicAppName = new TextView(App.context);
            String appName = String.format("%s", appRecord.appName);
            dynamicAppName.setText(appName);
            dynamicAppName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dynamicAppName.setTypeface(null, Typeface.BOLD);
            toolsAndAppsLinearLayout.addView(dynamicAppName);

            TextView dynamicAppToolMetadata = new TextView(App.context);
            String appMetadataString = String.format("%s \nRating: %.1f\n\n",
                    appRecord.description, appRecord.rating);
            dynamicAppToolMetadata.setText(appMetadataString + "\n");
            dynamicAppToolMetadata.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dynamicAppToolMetadata.setTypeface(null, Typeface.BOLD);
            toolsAndAppsLinearLayout.addView(dynamicAppToolMetadata);
        }
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

    public void storeCurrentState(ArrayList<AppRecord> appRecords, Activity activity) {

        StringBuilder appOrder = new StringBuilder();

        for (int i = 0; i < appRecords.size(); i++) {
            // clean up string
            String appName = appRecords.get(i).appName.trim();
            appOrder.append(appName);
            if ((i+1) < appRecords.size()) {
                appOrder.append(",");
            }
        }
        String appOrderString = appOrder.toString();
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(APP_SORT_ORDER.toString(), appOrderString);
        editor.commit();
    }

    public void restoreSavedState() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.appNameOrder = sharedPref.getString(APP_SORT_ORDER.toString(), null);
    }

    public ArrayList<AppRecord> sortApps(ArrayList<AppRecord> appRecords, String sortOrderString) {
        /**
         * sorts the apps based on the supplied sort order in the
         * comma separated string
         */
        // create a dictionary to avoid n^2 sort
        HashMap<String, AppRecord> appNameMap = new HashMap();
        for (AppRecord appRecord: appRecords) {
            // trim whitespace
            String appName = appRecord.appName.trim();
            appNameMap.put(appName, appRecord);
        }

        ArrayList<AppRecord> sortedApps = new ArrayList<>();
        String[] appNameOrder = sortOrderString.split(",");

        for (String appName : appNameOrder) {
            sortedApps.add(appNameMap.get(appName));
        }
        return sortedApps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreSavedState();
        if (this.appNameOrder != null && this.appNameOrder.isEmpty()) {
            // retrieve the app metadata
            AppMetadataRetriever appMetadataRetriever = new AppMetadataRetriever();
            ArrayList<AppRecord> appRecords = appMetadataRetriever.getAppMetadata();

            // sort the apps
            ArrayList<AppRecord> sortedRecords = sortApps(appRecords, appNameOrder);
            // set this instances app order variable
            this.currentApps = sortedRecords;
        } // else
        // no saved state, allow default creation
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        // only need to override on pause,
        // as this is always called before onStop and
        // onDestroy
        super.onPause();
        LinearLayout currView = (LinearLayout) root.findViewById(R.id.tools_and_apps_linear_layout);

        // retrieve the current list of apps and tools
        // order of elements:
        // icon, link to play store, name, metadata
        // get the total number of elements in the current search results
        int childCount = currView.getChildCount();
        // index that price elements start at
        int priceIndexStart = 4;
        // will hold the comma separated currency symbols
        StringBuilder cryptoSymbolsBuilder = new StringBuilder();

        // iterate over the elements, and extract the string that contains the currency symbol
        for (int i = (childCount - 1); i > priceIndexStart; ) {
            // each crypto currency view item consists of three distinct view elements
            // icon (image view), price change text view, crypto symbol text view, and metadata text view
            int symbolTextViewIndex = i - 1;
            TextView crypoToolView = (TextView) currView.getChildAt(symbolTextViewIndex);
            String cryptoSymbol = crypoToolView.getText().toString();
            cryptoSymbolsBuilder.append(cryptoSymbol);

            // decrement to indicate this view is saved
            i -= 4;
            // if this is not the last element, append a comma
            if (i > priceIndexStart) {
                cryptoSymbolsBuilder.append(",");
            }
        }

        storeCurrentState(currentApps, getActivity());
    }
}