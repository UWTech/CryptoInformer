package com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static java.util.Collections.sort;

public class AppMetadataRetriever {

    private ArrayList<AppRecord> apps;
    private ArrayList<String> appURIs;
    private ArrayList<String> appNames;
    private ArrayList<String> appCategory;
    private ArrayList<Double> appRatings;
    private ArrayList<String> description;
    private ArrayList<String> iconURLs;

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
        // TODO:: get icons via API or dependency injection
        this.iconURLs = new ArrayList<>();
        this.iconURLs.add("https://lh3.googleusercontent.com/LPjOvRg00ZLcmUvYbvELUX9qN46wUSRrDadTyAxkJIhP8GzUMIA1VZSWxB77BJ0yl1vl=s180");
        this.iconURLs.add("https://lh3.googleusercontent.com/oIyfxYGdhKM2Ne0nBDJDMxk_XVLzfQkzFJv-Va8oweJlntRG5puG9Ktg_6fIwE2flt0=s180");
        this.iconURLs.add("https://lh3.googleusercontent.com/MI293Gj1M2EEyJapun-FeEH1ka02taVYRQi5l7yfHFVYFNn1IVftBiBOoRCVyxfI=s180");
        this.iconURLs.add("https://lh3.googleusercontent.com/ePHTzGjnfXp_0hu-ir39QYr5WT6oeU4Axw04WxPhB905BvI8TTjOwUw3w6y_29iINQ=s180");
        this.iconURLs.add("https://lh3.googleusercontent.com/ZfuflYVRQ0eAKLJtd9mSnPIDJQEpBtKWV8G4MbxqFJeWlEIkHzHQSXMNjaLX4FuMn_8=s180");
        this.iconURLs.add("https://lh3.googleusercontent.com/LGcd3-1AWKcJKGtsmjQf6O8vSI8im98YgmIJOg4fKVAQ2VqF4yu8ImlU-KXW4H_D5jc=s180");
    }

    public ArrayList<AppRecord> getAppMetadata() {
        this.apps = new ArrayList<>();

        for (int i = 0; i < this.appNames.size(); i++) {
            AppRecord appRecord = new AppRecord(
                    this.appURIs.get(i),
                    this.iconURLs.get(i),
                    this.appNames.get(i),
                    this.appCategory.get(i),
                    this.description.get(i),
                    this.appRatings.get(i)
            );
            this.apps.add(appRecord);
        }
        return apps;
    }

    public ArrayList<AppRecord> sortHighToLow(ArrayList<AppRecord> appRecords) {

        // rely on overriden comparator to return sort
        Collections.sort(appRecords, new Comparator<AppRecord>() {
            @Override
            public int compare(AppRecord lhAppRecord, AppRecord rhApprecord) {
                return Double.compare(rhApprecord.rating, lhAppRecord.rating);
            }
        });
        return appRecords;
    }

    public ArrayList<AppRecord> sortLowToHigh(ArrayList<AppRecord> appRecords) {

        // rely on overriden comparator to return sort
        Collections.sort(appRecords, new Comparator<AppRecord>() {
            @Override
            public int compare(AppRecord lhAppRecord, AppRecord rhApprecord) {
                return Double.compare(lhAppRecord.rating, rhApprecord.rating);
            }
        });
        return appRecords;
    }

    public ArrayList<AppRecord> sortAlphabetically(ArrayList<AppRecord> appRecords) {

        // rely on overriden comparator to return sort
        Collections.sort(appRecords, new Comparator<AppRecord>() {
            @Override
            public int compare(AppRecord lhAppRecord, AppRecord rhApprecord) {
                int compareResult = lhAppRecord.appName.compareToIgnoreCase(rhApprecord.appName);
                return compareResult;
            }
        });
        return appRecords;
    }
}
