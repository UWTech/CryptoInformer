package com.example.cryptoinformer.ui.crypto_prices.price_feed;

public class PriceRecord {

    /**
     * simple object used to store price metadata
     */
    public String price;
    public String currName;
    public String currSymbol;
    public String logoURL;

    public PriceRecord(String price, String currName, String currSymbol, String logoURL) {
        this.currName = currName;
        this.price = price;
        this.currSymbol = currSymbol;
        this.logoURL = logoURL;
    }
}
