package com.example.cryptoinformer.ui.crypto_prices.price_feed;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CryptoPriceGenerator {
    /**
     * uses client to gather crypto prices,
     * and generates a list of price objects
     */
    private PriceFeedClient feedClient;
    private ArrayList<PriceRecord> cryptoPrices;
    // lookup keys for Crypto object
    private static String nameKey = "name";
    private static String priceKey = "price";
    private static String symbolKey = "symbol";
    private static String logoURLKey = "logo_url";
    private static String defaultIntervalKey = "1d";
    private static String priceChangeKey = "price_change";
    private static String priceChangePercentKey = "price_change_pct";
    public CryptoPriceGenerator(){
        this.feedClient = new PriceFeedClient();
        this.cryptoPrices = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<PriceRecord> retrieveCryptoPrices(ArrayList<String> symbols, String interval) {
        // default if no arg supplied
        ArrayList<String> targetCryptoSymbols = feedClient.cryptoSymbols;
        if (symbols != null) {
            targetCryptoSymbols = symbols;
        }
        String targetInterval = this.defaultIntervalKey;
        if (interval != null) {
            targetInterval = interval;
        }
        JSONArray resp = this.feedClient.getCryptoPrices(targetCryptoSymbols, targetInterval);
        this.cryptoPrices = convertResp(resp, targetInterval);
        return this.cryptoPrices;
    }

    private ArrayList<PriceRecord> convertResp(JSONArray resp, String interval) {

        ArrayList<PriceRecord> priceRecords = new ArrayList<>();

        for (int i = 0; i < resp.length(); i++) {
            try {
                JSONObject record = resp.getJSONObject(i);
                String currName = record.getString(this.nameKey);
                String price = record.getString(this.priceKey);
                String currSymbol = record.getString(this.symbolKey);
                String logoUrl = record.getString(this.logoURLKey);

                // get nested values
                JSONObject innerJson = record.getJSONObject(interval);
                String priceChange = innerJson.getString(this.priceChangeKey);
                String priceChangePercent = innerJson.getString(this.priceChangePercentKey);

                // create new Price record, and add to list
                PriceRecord priceRecord = new PriceRecord(price, currName, currSymbol, logoUrl, priceChange, priceChangePercent);
                priceRecords.add(priceRecord);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return priceRecords;
    }

    public ArrayList<PriceRecord> getPriceList() {
        return this.cryptoPrices;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<PriceRecord> searchForPrice(String symbol, String interval) {
        // capitalize for API
        symbol = symbol.toUpperCase();
        ArrayList<String> symbols = new ArrayList();
        symbols.add(symbol);

        JSONArray prices = feedClient.getCryptoPrices(symbols, interval);

        ArrayList<PriceRecord> priceRecods = convertResp(prices, interval);

        return priceRecods;
    }
}
