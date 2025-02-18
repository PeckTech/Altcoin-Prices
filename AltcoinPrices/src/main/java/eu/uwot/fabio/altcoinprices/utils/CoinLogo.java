package eu.uwot.fabio.altcoinprices.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class CoinLogo {

    private final Context context;

    public CoinLogo(Context context) {
        this.context = context.getApplicationContext();
    }

    // Get custom coin logo from URL an store it as Bitmap object //
    private void getLogoBitmap(String altcoinSymbol, String altcoinImageURL) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        Bitmap altcoinImageBitmap = null;

        try {
            url = new URL("https://www.cryptocompare.com" + altcoinImageURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            assert url != null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000); // 5 seconds timeout

            InputStream in = urlConnection.getInputStream();
            altcoinImageBitmap = BitmapFactory.decodeStream(in);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }

        saveCustomCoinBitmapLogoToStorage(altcoinSymbol, altcoinImageBitmap);
    }

    // Save custom coin bitmap logo to storage //
    private void saveCustomCoinBitmapLogoToStorage(String altcoinSymbol, Bitmap altcoinImageBitmap) {
        FileOutputStream outputStream;
        String filename = "logo_" + altcoinSymbol;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            altcoinImageBitmap = Bitmap.createScaledBitmap(altcoinImageBitmap, 96, 96, true);
            altcoinImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get custom coin bitmap logo from storage //
    public Bitmap loadCustomCoinBitmapLogoFromStorage(String altcoinName) {
        String filename = "logo_" + altcoinName;
        Bitmap altcoinImageBitmap = null;

        File directory = context.getFilesDir();
        File file = new File(directory, filename);

        try {
            FileInputStream inputStream = new FileInputStream(file);
            altcoinImageBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return altcoinImageBitmap;
    }

    // Remove custom coin bitmap logo from storage //
    public void removeCustomCoinBitmapLogoFromStorage(String altcoinName) {
        String filename = "logo_" + altcoinName;

        File directory = context.getFilesDir();
        File file = new File(directory, filename);
        file.delete();
    }

    // Get Coin Logo from cryptocompare
    public void getCoinLogo(String coinSymbol) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder dataSTR = new StringBuilder();
        String tradingPair;
        String altcoinImageURL = null;

        if (coinSymbol.equals("BTC")) {
            tradingPair = "USD";
        } else {
            tradingPair = "BTC";
        }

        try {
            if (new Coin(context, false).isItIconomiDaa(coinSymbol)) {
                url = new URL("https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=" +
                        "ICN" +
                        "&tsym=" +
                        tradingPair);
            } else {
                url = new URL("https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=" +
                        coinSymbol +
                        "&tsym=" +
                        tradingPair);
            }
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
            JSONObject jsonNested = json.getJSONObject("Data").getJSONObject("CoinInfo");
            altcoinImageURL = jsonNested.getString("ImageUrl");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getLogoBitmap(coinSymbol, altcoinImageURL);
    }

    public boolean coinLogoExists(String altcoinSymbol) {
        File directory = context.getFilesDir();
        File file = new File(directory, "logo_" + altcoinSymbol);
        return file.exists();
    }

    public void removeCachedLogos() {
        File directory = context.getFilesDir();
        for(File file: directory.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

}
