package eu.uwot.fabio.altcoinprices.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import eu.uwot.fabio.altcoinprices.utils.Coin;
import eu.uwot.fabio.altcoinprices.utils.CoinLogo;
import eu.uwot.fabio.altcoinprices.R;

public class EditCustomCoinActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private EditText altcoinSymbol_text;
    private String altcoinSymbol;
    private String altcoinSymbolInitial;
    private String altcoinDescription;
    private EditText tradingviewExchangeName_text;
    private String tradingviewExchangeName;
    private EditText tradingviewTradingPair_text;
    private String tradingviewTradingPair;
    private String key;
    private boolean noErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_custom_coin);

        prefs = getApplicationContext().getSharedPreferences("Settings", 0);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            key = b.getString("key");
            loadCustomCoinData(key);
        }

        // Init text fields
        altcoinSymbol_text = findViewById(R.id.altcoinSymbol_text);
        altcoinSymbol_text.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        altcoinSymbol_text.setText(altcoinSymbol);
        altcoinSymbol_text.setTextColor(getResources().getColor(R.color.colorTextSeconday));
        altcoinSymbol_text.setEnabled(false);
        tradingviewExchangeName_text = findViewById(R.id.tradingviewExchangeName_text);
        tradingviewExchangeName_text.setText(tradingviewExchangeName);
        tradingviewTradingPair_text = findViewById(R.id.tradingviewTradingPair_text);
        tradingviewTradingPair_text.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        tradingviewTradingPair_text.setText(tradingviewTradingPair);

        // Save Button
        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                altcoinSymbol = altcoinSymbol_text.getText().toString().toUpperCase();
                tradingviewExchangeName = tradingviewExchangeName_text.getText().toString().toLowerCase();
                tradingviewTradingPair = tradingviewTradingPair_text.getText().toString().toUpperCase();
                Coin coin = new Coin(getApplicationContext(), false);

                if ((coin.coinsLabelGraph.get(altcoinSymbol) != null) && (!altcoinSymbol.equals(altcoinSymbolInitial))) {
                    Toast.makeText(getApplicationContext(), R.string.coinAlreadyPresent, Toast.LENGTH_SHORT).show();
                } else if (!altcoinSymbol.equals("")) {
                    Thread getCoinDataTh = new Thread() {
                        public void run() {
                            getCoinData();

                            if (noErrors) {
                                editCustomCoin();
                                startActivity(new Intent(getApplicationContext(), ManageCustomCoinsActivity.class));
                            }
                        }
                    };

                    noErrors = true;
                    getCoinDataTh.start();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back Button
        final Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeCustomCoinKey();
                startActivity(new Intent(getApplicationContext(), ManageCustomCoinsActivity.class));
            }
        });
    }

    // Get coin data from cryptocompare.com //
    // REQUEST: https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=BTC&tsym=USD
    // RESPONSE: too long to be here
    private void getCoinData() {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder dataSTR = new StringBuilder();
        String tradingPair;

        if (altcoinSymbol.equals("BTC")) {
            tradingPair = "USD";
        } else {
            tradingPair = "BTC";
        }

        try {
            url = new URL("https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=" +
                    altcoinSymbol +
                    "&tsym=" +
                    tradingPair);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            assert url != null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000); // 5 seconds timeout

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                dataSTR.append(current);
            }
        } catch (SocketTimeoutException e) {
            dataSTR = new StringBuilder("0");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }

        JSONObject json;
        altcoinDescription = "";

        try {
            json = new JSONObject(dataSTR.toString());
            JSONObject jsonNested = json.getJSONObject("Data").getJSONObject("CoinInfo");
            altcoinDescription = jsonNested.getString("FullName");
        } catch (JSONException e) {
            noErrors = false;
            e.printStackTrace();
        }
    }

    // Add back modified custom coin //
    private void editCustomCoin() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = prefs.edit();
        JSONObject json = new JSONObject();
        JSONObject newCustomCoin = new JSONObject();

        // Remove old custom coin
        removeCustomCoinKey();

        // Load custom coins data
        try {
            json = new JSONObject(prefs.getString("customCoins", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set empty fields to "na"
        if (tradingviewExchangeName.equals("")) {
            tradingviewExchangeName = "na";
            tradingviewTradingPair = "na";
        }

        // Create new coin JSON array
        try {
            newCustomCoin.put("altcoinSymbol", altcoinSymbol);
            newCustomCoin.put("altcoinDescription", altcoinDescription);
            newCustomCoin.put("tradingviewExchangeName", tradingviewExchangeName);
            newCustomCoin.put("tradingviewTradingPair", tradingviewTradingPair);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add new custom coin to custom coins JSON
        try {
            json.put(altcoinSymbol, newCustomCoin);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString("customCoins", json.toString());
        editor.apply();
    }

    // Load portfolio items array //
    private void loadCustomCoinData(String key) {
        prefs = getApplicationContext().getSharedPreferences("Settings", 0);

        String jsonData = prefs.getString("customCoins", "");

        try {
            JSONObject json = new JSONObject(jsonData).getJSONObject(key);
            altcoinSymbol = json.getString("altcoinSymbol");
            altcoinSymbolInitial = altcoinSymbol;
            tradingviewExchangeName = json.getString("tradingviewExchangeName");
            tradingviewTradingPair = json.getString("tradingviewTradingPair");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Remove custom coin //
    private void removeCustomCoinKey() {
        JSONObject json = new JSONObject();

        try {
            json = new JSONObject(prefs.getString("customCoins", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        json.remove(key);

        editor = prefs.edit();
        editor.putString("customCoins", json.toString());
        editor.apply();

        // Remove custom coin logo bitmap file
        new CoinLogo(getApplicationContext()).removeCustomCoinBitmapLogoFromStorage(key);

        // Remove custom coin from portfolio if present
        new Coin(getApplicationContext(), false).removeItem(key);
    }
}
