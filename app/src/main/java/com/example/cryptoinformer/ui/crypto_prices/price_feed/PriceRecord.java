package com.example.cryptoinformer.ui.crypto_prices.price_feed;

public class PriceRecord {

    /**
     * simple object used to store price metadata
     */
    public String price;
    public String priceChange;
    public String getPriceChangePercent;
    public String currName;
    public String currSymbol;
    public String logoURL;

    public PriceRecord(String price, String currName, String currSymbol, String logoURL, String priceChange, String getPriceChangePercent) {
        this.currName = currName;
        this.price = price;
        this.currSymbol = currSymbol;
        this.logoURL = logoURL;
        this.priceChange = priceChange;
        this.getPriceChangePercent = getPriceChangePercent;
    }
}
