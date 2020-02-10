package com.example.cryptoinformer.ui.crypto_prices.price_feed;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PriceFeedClient {
    /**
     * contains methods used for accessing nomics CryptoCurrency APIs
     */

    // list of abbreviations for crypto currencies to be queried
    private ArrayList<String> cryptoSymbols;
    // key used to access price feed API
    private String cryptoApiKey;
    private String cryptoApiURI;

    public PriceFeedClient() {
        // TODO:: use environment for dependency injection
        this.cryptoApiKey = "7bdd3e97382e1313ab309c1397ff8589";
        this.cryptoApiURI = "https://api.nomics.com/v1/currencies/ticker";
        this.cryptoSymbols = new ArrayList<>();
        this.cryptoSymbols.add("BTC");
        this.cryptoSymbols.add("ETH");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private URL generateApiURL(String apiToken, ArrayList<String> symbols, String interval, String currency) throws MalformedURLException {
        /**
         *  generates the URL with supplied parameters as GET request formatted query params
         */
        String urlString = this.cryptoApiURI + "?key=" + apiToken + "&ids=" + String.join(",", symbols) +
                "&interval=" + interval + "&convert=" + currency;

        URL apiURL = new URL(urlString);
        return apiURL;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONArray getCryptoPrices() {
        try {
            URL apiUrl = generateApiURL(this.cryptoApiKey, this.cryptoSymbols, "1d", "USD");
            InputStream cryptoStream = apiUrl.openConnection().getInputStream();

            BufferedReader respBuffer = new BufferedReader(new InputStreamReader(cryptoStream));

            StringBuilder cryptoRespBuilder = new StringBuilder();
            // gather all the input
            String respLine = null;
            while ((respLine = respBuffer.readLine()) != null) {
                cryptoRespBuilder.append(respLine);
            }
            cryptoStream.close();
            // convert to JSON
            JSONArray jsonRes = new JSONArray(cryptoRespBuilder.toString());
            return jsonRes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO:: error handling framework
        return null;
    }
}
