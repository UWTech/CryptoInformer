package com.example.cryptoinformer.ui.crypto_tools_and_apps.app_metadata;

public class AppRecord {
    /**
     * simple class used to contain metdata about a given app
     */

    public String appUri;
    public String iconUri;
    public String appName;
    public String category;
    public String description;
    public double rating;

    public AppRecord(String appUri, String iconUri, String appName, String category, String description, double rating) {
        this.appUri = appUri;
        this.iconUri = iconUri;
        this.appName = appName;
        this.category = category;
        this.description = description;
        this.rating = rating;
    }
}
