package eu.uwot.fabio.altcoinprices.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import eu.uwot.fabio.altcoinprices.R;

public class Coin {

    public String[] coins = CoinList.coins;
    private final String[] iconomi_daa = CoinList.iconomi_daa;
    public String[] descriptions = CoinList.descriptions;
    public String[] coinsLabelDescriptionsString;
    public Hashtable<String, String> coinsLabelDescriptionHashtable;
    public Hashtable<String, String> coinsDescriptionLabelHashtable;
    private final Hashtable<String, String> coinsLabelExchangeHashtable = CoinList.coinsLabelExchangeHashtable;
    public final Hashtable<String, String> coinsLabelGraph = CoinList.coinsLabelGraph;
    private final Hashtable<String, String> tradingviewCoinSymbol = CoinList.tradingviewCoinSymbol;
    public final String [] currencies = new String[] {
            "USD",
            "EUR",
            "GBP",
            "CNY",
            "JPY",
            "RUB",
            "CAD",
            "AUD",
            "INR",
            "KRW",
            "CHF",
    };

    private final Context context;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private float btcusd;
    private float btceur;
    private float btcgbp;
    private float btccny;
    private float btcjpy;
    private float btcrub;
    private float btccad;
    private float btcaud;
    private float btcinr;
    private float btckrw;
    private float btcchf;

    public Coin(Context context, boolean doBTCUSDEUR) {
        this.context = context.getApplicationContext();
        prefs = context.getSharedPreferences("Settings", 0);

        if (doBTCUSDEUR) {
            btcusd = prefs.getFloat("btcusd", 1);
            btceur = prefs.getFloat("btceur", 1);
            btcgbp = prefs.getFloat("btcgbp", 1);
            btccny = prefs.getFloat("btccny", 1);
            btcjpy = prefs.getFloat("btcjpy", 1);
            btcrub = prefs.getFloat("btcrub", 1);
            btccad = prefs.getFloat("btccad", 1);
            btcaud = prefs.getFloat("btcaud", 1);
            btcinr = prefs.getFloat("btcinr", 1);
            btckrw = prefs.getFloat("btckrw", 1);
            btcchf = prefs.getFloat("btcchf", 1);
        }

        initCoins();
    }

    private void initCoins() {
        coinsLabelDescriptionsString = new String[coins.length];
        for (int i = 0; i < coins.length; i++) {
            coinsLabelDescriptionsString[i] = coins[i] + " - " + descriptions[i];
        }

        coinsLabelDescriptionHashtable = new Hashtable<>();
        for (int i = 0; i < coins.length; i++) {
            coinsLabelDescriptionHashtable.put(coins[i], descriptions[i]);
        }

        coinsDescriptionLabelHashtable = new Hashtable<>();
        coinsDescriptionLabelHashtable.put("BTC/USD - Bitcoin", "BTCUSD");
        coinsDescriptionLabelHashtable.put("ETH/USD - Ethereum", "ETHUSD");
        for (int i = 0; i < coins.length; i++) {
            coinsDescriptionLabelHashtable.put(coins[i] + " - " + descriptions[i], coins[i]);
        }

        loadCustomCoinsData();
    }

    // Get which exchange trade a coin //
    public String getCoinExchange (String altcoinName) {
        return coinsLabelExchangeHashtable.get(altcoinName);
    }

    public boolean isItIconomiDaa (String altcoinName) {
        for (String anIconomi_daa : iconomi_daa) {
            if (altcoinName.equals(anIconomi_daa)) {
                return true;
            }
        }

        return false;
    }

    public void addItem(String altcoinDesc, float amountBought, float unitPrice, String currency, long unixTimestamp) {
        prefs = context.getApplicationContext().getSharedPreferences("Settings", 0);
        editor = prefs.edit();

        String altcoinLabel = coinsDescriptionLabelHashtable.get(altcoinDesc);
        float unitPriceBTC;

        assert altcoinLabel != null;
        if (altcoinLabel.equals("BTC")) {
            unitPriceBTC = 1f;
        } else {
            unitPriceBTC = getCoinValue(altcoinLabel, "BTC", unixTimestamp);
        }

        float amountBought_old = prefs.getFloat(altcoinLabel + "_a", -1f);

        if (amountBought_old != -1f) {
            // Amount can't be a negative number
            if (amountBought_old + amountBought > 0) {
                String altcoinCurrency = prefs.getString(altcoinLabel + "_currency", "EUR");
                float p_btc = prefs.getFloat(altcoinLabel + "_p_btc", 0f);
                float unitPrice_old = prefs.getFloat(altcoinLabel + "_p", -1f);

                // Convert value to the correct currency if needed
                assert altcoinCurrency != null;
                if (!altcoinCurrency.equals(currency)) {
                    unitPrice = currencyToCurrency(unitPrice, altcoinCurrency, currency);
                }

                unitPrice = (amountBought_old * unitPrice_old + amountBought * unitPrice) / (amountBought_old + amountBought);
                unitPriceBTC = ((amountBought_old * p_btc) + (amountBought * unitPriceBTC)) / (amountBought_old + amountBought);
                amountBought = amountBought_old + amountBought;
            } else {
                Toast.makeText(context, R.string.errorNegativeAmount, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            editor.putString(altcoinLabel + "_currency", currency);
        }

        editor.putFloat(altcoinLabel + "_p_btc", unitPriceBTC);
        editor.putFloat(altcoinLabel + "_a", amountBought);
        editor.putFloat(altcoinLabel + "_p", unitPrice);
        editor.apply();
    }

    public void editItem(String altcoinLabel, float amountBought, float unitPrice, long unixTimestamp) {
        prefs = context.getApplicationContext().getSharedPreferences("Settings", 0);
        editor = prefs.edit();

        if ((amountBought >= 0) && (unitPrice >= 0)) {
            if ((!altcoinLabel.equals("BTC")) && (unixTimestamp != 0L)) {
                float unitPriceBTC = getCoinValue(altcoinLabel, "BTC", unixTimestamp);
                editor.putFloat(altcoinLabel + "_p_btc", unitPriceBTC);
            }

            editor.putFloat(altcoinLabel + "_a", amountBought);
            editor.putFloat(altcoinLabel + "_p", unitPrice);
            editor.apply();
        } else {
            Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    public void removeItem(String altcoinName) {
        prefs = context.getApplicationContext().getSharedPreferences("Settings", 0);
        editor = prefs.edit();

        editor.remove(altcoinName + "_a");
        editor.remove(altcoinName + "_p");
        editor.remove(altcoinName + "_currentUnitValue");
        editor.remove(altcoinName + "_lastDayUnitValue");
        editor.remove(altcoinName + "_currency");
        editor.remove(altcoinName + "_p_btc");
        editor.remove(altcoinName + "_currentUnitValue_btc");
        editor.remove(altcoinName + "_lastDayUnitValue_btc");

        editor.apply();
    }

    // Get coin value from cryptocompare.com //
    public float getCoinValue(String altcoinName, String currency, long unixTimestamp) {
        float coinValue;

        if (getCoinExchange(altcoinName).equals("cryptocompare_icndaa")) {
            coinValue = getCoinQuoteIconomiDDA(altcoinName, currency);
        } else {
            coinValue = getCoinQuoteCryptoCompare(altcoinName, currency, unixTimestamp);
        }

        // Exchange API are down or reporting broken values
        editor = prefs.edit();

        if (coinValue == -1f) {
            coinValue = getCoinInitialValue(altcoinName);
            editor.putBoolean("apiError", true);
            editor.apply();
        } else {
            editor.putBoolean("apiError", false);
            editor.apply();
        }

        return coinValue;
    }

    // Get coin change in _currency_ from https://api.iconomi.com //
    // CURRENT    REQUEST: https://api.iconomi.com/v1/daa/BLX/price
    // CURRENT    RESPONSE: {"ticker":"BLX","currency":"USD","price":"1.18766689"}
    private float getCoinQuoteIconomiDDA(String altcoinSymbol, String currency) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder dataSTR = new StringBuilder();
        float coinQuote = -1f;

        try {
            url = new URL("https://api.iconomi.com/v1/daa/" + altcoinSymbol + "/price");
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

        try {
            json = new JSONObject(dataSTR.toString());
            coinQuote = (float) json.getDouble("price");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        coinQuote = currencyToCurrency(coinQuote, "USD", currency);

        return coinQuote;
    }

    // Get coin change in _currency_ from cryptocompare.com //
    // CURRENT    REQUEST: https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=USD
    // CURRENT    RESPONSE: {"USD":6945.12}
    // HISTORICAL REQUEST: https://min-api.cryptocompare.com/data/pricehistorical?fsym=BTC&tsyms=USD&ts=1524831212960
    // HISTORICAL RESPONSE: {"BTC":{"USD":9254.98}}
    private float getCoinQuoteCryptoCompare(String altcoinSymbol, String currency, long unixTimestamp) {
        String apiKey = prefs.getString("cryptocompareAPI", "");
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder dataSTR = new StringBuilder();
        float coinQuote = -1f;

        if (unixTimestamp == 0) {
            try {
                url = new URL("https://min-api.cryptocompare.com/data/price?fsym=" +
                        altcoinSymbol +
                        "&tsyms=" +
                        currency +
                        "&apiKey=" +
                        apiKey);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                url = new URL("https://min-api.cryptocompare.com/data/pricehistorical?fsym=" +
                        altcoinSymbol +
                        "&tsyms=" +
                        currency +
                        "&ts=" +
                        unixTimestamp +
                        "&apiKey=" +
                        apiKey);
                Log.d("COIN", url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
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

        try {
            if (unixTimestamp == 0) {
                json = new JSONObject(dataSTR.toString());
            } else {
                json = new JSONObject(dataSTR.toString()).getJSONObject(altcoinSymbol);
            }

            coinQuote = (float) json.getDouble(currency);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return coinQuote;
    }

    // Get BTC trading pairs from cryptocompare.com //
    // CURRENT    REQUEST: https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC&tsyms=USD,EUR,CND
    // CURRENT    RESPONSE: {"BTC":{"USD":7237.55,"EUR":6199.56,"CND":125313.28}}
    public float[] getBTCQuoteCryptoCompare(String [] currencies) {
        String apiKey = prefs.getString("cryptocompareAPI", "");
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder dataSTR = new StringBuilder();
        int currenciesNumber = 11;
        float[] coinQuote = new float[currenciesNumber];
        StringBuilder tradingPairsString = new StringBuilder();

        // Build trading pairs String
        for (int i = 0; i< currencies.length - 1; i++) {
            tradingPairsString.append(currencies[i]).append(",");
        }
        tradingPairsString.append(currencies[currenciesNumber - 1]);

        try {
            url = new URL("https://min-api.cryptocompare.com/data/price?fsym=" +
                    "BTC" +
                    "&tsyms=" +
                    tradingPairsString +
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

        if (!dataSTR.toString().equals("")) {
            JSONObject json;

            try {
                json = new JSONObject(dataSTR.toString());

                for (int i = 0; i < currenciesNumber; i++) {
                    coinQuote[i] = (float) json.getDouble(currencies[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 0; i < currenciesNumber; i++) {
                coinQuote[i] = 1;
            }
        }

        return coinQuote;
    }

    // Convert currencies //
    public float currencyToCurrency(float price, String currency, String newCurrency) {
        float change0 = getChange(currency);
        float change1 = getChange(newCurrency);

        return price * change1 / change0;
    }

    // Get trading pair //
    private float getChange(String currency) {
        float change = -1f;

        switch (currency) {
            case "USD":
                change = btcusd;
                break;
            case "EUR":
                change = btceur;
                break;
            case "GBP":
                change = btcgbp;
                break;
            case "CNY":
                change = btccny;
                break;
            case "JPY":
                change = btcjpy;
                break;
            case "RUB":
                change = btcrub;
                break;
            case "CAD":
                change = btccad;
                break;
            case "AUD":
                change = btcaud;
                break;
            case "INR":
                change = btcinr;
                break;
            case "KRW":
                change = btckrw;
                break;
            case "CHF":
                change = btcchf;
                break;
            case "BTC":
                change = 1f;
                break;
        }

        return change;
    }

    // Get the amount of FIAT a coin was paid //
    private float getCoinInitialValue(String altcoinName) {
        prefs = context.getApplicationContext().getSharedPreferences("Settings", 0);

        return prefs.getFloat(altcoinName + "_p", -1f);
    }

    // Load portfolio items array //
    private void loadCustomCoinsData() {
        String jsonData = prefs.getString("customCoins", "");

        assert jsonData != null;
        if (jsonData.length() != 0) {
            try {
                JSONObject json = new JSONObject(jsonData);
                int size = json.length();

                if (size >= 1) {
                    int firstFree = coins.length;
                    expandArrays(size);
                    Iterator<?> keys = json.keys();

                    while (keys.hasNext()) {
                        String altcoinSymbol, altcoinDescription, tradingviewExchangeName, tradingviewTradingPair;
                        String key = (String) keys.next();

                        JSONObject jsonCoinDetails = json.getJSONObject(key);
                        altcoinSymbol = jsonCoinDetails.getString("altcoinSymbol");
                        altcoinDescription = jsonCoinDetails.getString("altcoinDescription");
                        tradingviewExchangeName = jsonCoinDetails.getString("tradingviewExchangeName");
                        tradingviewTradingPair = jsonCoinDetails.getString("tradingviewTradingPair");

                        addCustomCoin(altcoinSymbol,
                                altcoinDescription,
                                tradingviewExchangeName,
                                tradingviewTradingPair,
                                firstFree);

                        firstFree++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Exapand arrays size to fit custom coins //
    private void expandArrays(int size) {
        coins = Arrays.copyOf(coins, coins.length + size);
        descriptions = Arrays.copyOf(descriptions, descriptions.length + size);
        coinsLabelDescriptionsString = Arrays.copyOf(coinsLabelDescriptionsString, coinsLabelDescriptionsString.length + size);
    }

    // Add custom coin //
    private void addCustomCoin(String altcoinSymbol, String altcoinDescription,
                               String tradingviewExchangeName, String tradingviewTradingPair,
                               int firstFree) {
        coins[firstFree] = altcoinSymbol;
        descriptions[firstFree] = altcoinDescription;
        coinsLabelDescriptionsString[firstFree] = coins[firstFree] + " - " + descriptions[firstFree];
        coinsLabelDescriptionHashtable.put(coins[firstFree], descriptions[firstFree]);
        coinsDescriptionLabelHashtable.put(coins[firstFree] + " - " + descriptions[firstFree], coins[firstFree]);

        coinsLabelExchangeHashtable.put(coins[firstFree], tradingviewExchangeName);
        coinsLabelGraph.put(coins[firstFree], tradingviewTradingPair);
    }

    // Return TradingView Coin Symbol //
    public String getTradingviewCoinSymbol(String altcoinSymbol) {
        return tradingviewCoinSymbol.get(altcoinSymbol);
    }

}
