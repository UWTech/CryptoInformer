package com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata;

import java.util.ArrayList;

public class AppMetadataRetriever {

    private ArrayList<AppRecord> apps;
    private ArrayList<String> appURIs;
    private ArrayList<String> appNames;
    private ArrayList<String> appCategory;
    private ArrayList<Double> appRatings;
    private ArrayList<String> description;

    public AppMetadataRetriever() {
        this.apps = new ArrayList<>();
        this.appURIs = new ArrayList<>();
        // add currently supported app URIs
        // TODO:: replace with dependency injection
        this.appURIs.add("https://play.google.com/store/apps/details?id=co.mona.android");
        this.appURIs.add("https://play.google.com/store/apps/details?id=com.blockfolio.blockfolio");
        this.appURIs.add("https://play.google.com/store/apps/details?id=com.crypter.cryptocyrrency");
        this.appURIs.add("https://play.google.com/store/apps/details?id=com.coinstats.crypto.portfolio");
        this.appURIs.add("https://play.google.com/store/apps/details?id=com.zeykit.dev.cryptomarketcap");
        this.appURIs.add("https://play.google.com/store/apps/details?id=com.binance.dev");
        // TODO:: get info from APIs via URI
        this.appNames = new ArrayList<>();
        this.appNames.add("Crypto.com");
        this.appNames.add("Blockfolio ");
        this.appNames.add("Crypto App");
        this.appNames.add("Coin Stats App");
        this.appNames.add("Crypto Market Cap");
        this.appNames.add("Binance Exchange");
        // TODO:: get info from APIs via URI
        this.appCategory = new ArrayList<>();
        this.appCategory.add("finance");
        this.appCategory.add("finance");
        this.appCategory.add("finance");
        this.appCategory.add("finance");
        this.appCategory.add("finance");
        this.appCategory.add("finance");
        // TODO:: get info from APIs via URI
        this.appRatings = new ArrayList<>();
        this.appRatings.add(4.2);
        this.appRatings.add(4.8);
        this.appRatings.add(4.7);
        this.appRatings.add(4.6);
        this.appRatings.add(4.7);
        this.appRatings.add(4.5);
        // TODO:: get info from APIs via URI
        this.description = new ArrayList<>();
        this.description.add("Buy Bitcoin Now");
        this.description.add("Bitcoin and Cryptocurrency Tracker");
        this.description.add("Widgets, Alerts, News, Bitcoin Prices");
        this.description.add("Crypto Tracker & Bitcoin Priceso");
        this.description.add("Crypto tracker, Alerts, News");
        this.description.add("Crypto Trading");
    }

    public ArrayList<AppRecord> getAppMetadata() {
        for (int i = 0; i < this.appNames.size(); i++) {
            AppRecord appRecord = new AppRecord(
                    this.appURIs.get(i),
                    null,
                    this.appNames.get(i),
                    this.appCategory.get(i),
                    this.description.get(i),
                    this.appRatings.get(i)
            );
            this.apps.add(appRecord);
        }
        return apps;
    }

}
