package eu.uwot.fabio.altcoinprices.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.uwot.fabio.altcoinprices.utils.Coin;
import eu.uwot.fabio.altcoinprices.utils.CoinLogo;
import eu.uwot.fabio.altcoinprices.R;
import eu.uwot.fabio.altcoinprices.utils.UnixTimestamp;

public class LoadingActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Coin coin;
    private ProgressBar progressBar;
    private int progressBarSlotTime;
    private int progressBarProgess;
    private boolean keepRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);

        final Thread retrieveData = new Thread() {
            public void run() {
                setPortfolioData();
                if (keepRunning) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };

        retrieveData.start();

        TextView skip = findViewById(R.id.skipView);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keepRunning = false;
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void setPortfolioData () {
        prefs = getApplicationContext().getSharedPreferences("Settings", 0);
        editor = prefs.edit();
        coin = new Coin(getApplicationContext(), true);
        CoinLogo coinLogo = new CoinLogo(getApplicationContext());
        boolean initCoinsLogos = prefs.getBoolean("initCoinsLogos", true);

        int portfolioItems = 0;
        for (int i = 0; i < coin.coins.length; i ++) {
            float amountBought = prefs.getFloat(coin.coins[i] + "_a", -1f);

            if (amountBought != -1f) {
                portfolioItems += 1;
            }
        }

        progressBarSlotTime = 100 / (portfolioItems + 1);
        progressBarProgess = 0;

        initTradingPair();

        progressBar.setProgress(progressBarProgess += progressBarSlotTime);

        for (int i = 0; i < coin.coins.length; i ++) {
            float amountBought = prefs.getFloat(coin.coins[i] + "_a", -1f);

            if (amountBought != -1f) {
                if (coin.getCoinExchange(coin.coins[i]).equals("cryptocompare_icndaa")) {
                    String altcoinCurrency = prefs.getString(coin.coins[i] + "_currency", "EUR");
                    float currentUnitValue = coin.getCoinValue(coin.coins[i], altcoinCurrency, 0L);
                    editor.putFloat(coin.coins[i] + "_currentUnitValue", currentUnitValue);
                    editor.putFloat(coin.coins[i] + "_lastDayUnitValue", currentUnitValue);

                    float unitValue_btc = coin.currencyToCurrency(currentUnitValue, altcoinCurrency, "BTC");
                    editor.putFloat(coin.coins[i] + "_currentUnitValue_btc", unitValue_btc);
                    editor.putFloat(coin.coins[i] + "_lastDayUnitValue_btc", unitValue_btc);
                } else {
                    String altcoinCurrency = prefs.getString(coin.coins[i] + "_currency", "EUR");
                    float currentUnitValue = coin.getCoinValue(coin.coins[i], altcoinCurrency, 0L);
                    editor.putFloat(coin.coins[i] + "_currentUnitValue", currentUnitValue);

                    float lastDayUnitValue = coin.getCoinValue(coin.coins[i], altcoinCurrency, new UnixTimestamp().getYesterdayUnixTimestamp());
                    editor.putFloat(coin.coins[i] + "_lastDayUnitValue", lastDayUnitValue);

                    float lastDayUnitValue_btc, currentUnitValue_btc;
                    if (coin.coins[i].equals("BTC")) {
                        currentUnitValue_btc = 1f;
                        lastDayUnitValue_btc = 1f;
                    } else {
                        currentUnitValue_btc = coin.getCoinValue(coin.coins[i], "BTC", 0L);
                        lastDayUnitValue_btc = coin.getCoinValue(coin.coins[i], "BTC", new UnixTimestamp().getYesterdayUnixTimestamp());
                    }

                    editor.putFloat(coin.coins[i] + "_currentUnitValue_btc", currentUnitValue_btc);
                    editor.putFloat(coin.coins[i] + "_lastDayUnitValue_btc", lastDayUnitValue_btc);
                }

                if (initCoinsLogos) {
                    if (!coinLogo.coinLogoExists(coin.coins[i])) {
                        coinLogo.getCoinLogo(coin.coins[i]);
                    }
                }

                progressBarProgess += progressBarSlotTime;
                progressBar.setProgress(progressBarProgess);
            }
        }

        editor.putBoolean("initCoinsLogos", false);
        editor.apply();
    }

    private void initTradingPair() {
        float [] changes = coin.getBTCQuoteCryptoCompare(coin.currencies);

        editor.putFloat("btcusd", changes[0]);
        editor.putFloat("btceur", changes[1]);
        editor.putFloat("btcgbp", changes[2]);
        editor.putFloat("btccny", changes[3]);
        editor.putFloat("btcjpy", changes[4]);
        editor.putFloat("btcrub", changes[5]);
        editor.putFloat("btccad", changes[6]);
        editor.putFloat("btcaud", changes[7]);
        editor.putFloat("btcinr", changes[8]);
        editor.putFloat("btckrw", changes[9]);
        editor.putFloat("btcchf", changes[10]);
        editor.apply();
    }
}
