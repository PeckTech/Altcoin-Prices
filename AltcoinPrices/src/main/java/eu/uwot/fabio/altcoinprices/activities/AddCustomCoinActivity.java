package eu.uwot.fabio.altcoinprices.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import eu.uwot.fabio.altcoinprices.R;

public class AddCustomCoinActivity extends AppCompatActivity {

    private EditText altcoinSymbol_text;
    private String altcoinSymbol;
    private String altcoinDescription;
    private EditText tradingviewExchangeName_text;
    private String tradingviewExchangeName;
    private EditText tradingviewTradingPair_text;
    private String tradingviewTradingPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_coin);

        altcoinSymbol_text = findViewById(R.id.altcoinSymbol_text);
        altcoinSymbol_text.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        tradingviewExchangeName_text = findViewById(R.id.tradingviewExchangeName_text);
        tradingviewTradingPair_text = findViewById(R.id.tradingviewTradingPair_text);
        tradingviewTradingPair_text.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // Save Button //
        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                altcoinSymbol = altcoinSymbol_text.getText().toString().toUpperCase();
                tradingviewExchangeName = tradingviewExchangeName_text.getText().toString().toLowerCase();
                tradingviewTradingPair = tradingviewTradingPair_text.getText().toString().toUpperCase();
                Coin coin = new Coin(getApplicationContext(), false);

                if (coin.coinsLabelGraph.get(altcoinSymbol) != null) {
                    Toast.makeText(getApplicationContext(), R.string.coinAlreadyPresent, Toast.LENGTH_SHORT).show();
                } else if (!altcoinSymbol.equals("")) {
                    new GetCoinDataTask().execute();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back Button //
        final Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ManageCustomCoinsActivity.class));
            }
        });
    }


    private class GetCoinDataTask extends AsyncTask<URL, Void, Long> {
        protected Long doInBackground(URL... urls) {
            int res = getCoinData(getApplicationContext());

            if (res == 1) {
                addCustomCoin();
                return 1L;
            } else {
                return 0L;
            }
        }

        protected void onPostExecute(Long result) {
            if (result != 1) {
                Toast.makeText(getApplicationContext(), R.string.errorCoinNotFound, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(getApplicationContext(), ManageCustomCoinsActivity.class));
            }
        }
    }


    // Get coin data from cryptocompare.com //
    // REQUEST: https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=BTC&tsym=USD
    // RESPONSE: too long to be here
    private int getCoinData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Settings", 0);
        String apiKey = prefs.getString("cryptocompareAPI", "");
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
                    tradingPair +
                    "&apiKey=" +
                    apiKey);
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
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Add custom coin //
    private void addCustomCoin() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = prefs.edit();
        JSONObject json = new JSONObject();
        JSONObject newCustomCoin = new JSONObject();

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

        // add new custom coin to custom coins JSON
        try {
            json.put(altcoinSymbol, newCustomCoin);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString("customCoins", json.toString());
        editor.apply();
    }

}
