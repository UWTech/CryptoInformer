package com.example.cryptoinformer.ui.crypto_prices.price_feed;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
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

    public CryptoPriceGenerator(){
        this.feedClient = new PriceFeedClient();
        this.cryptoPrices = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<PriceRecord> retrieveCryptoPrices() {
        JSONArray resp = this.feedClient.getCryptoPrices();
        this.cryptoPrices = convertResp(resp);
        return this.cryptoPrices;
    }

    private ArrayList<PriceRecord> convertResp(JSONArray resp) {

        ArrayList<PriceRecord> priceRecords = new ArrayList<>();

        for (int i = 0; i < resp.length(); i++) {
            try {
                JSONObject record = resp.getJSONObject(i);
                String currName = record.getString(this.nameKey);
                String price = record.getString(this.priceKey);
                String currSymbol = record.getString(this.symbolKey);
                String logoUrl = record.getString(this.logoURLKey);

                // create new Price record, and add to list
                PriceRecord priceRecord = new PriceRecord(price, currName, currSymbol, logoUrl);
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
}
