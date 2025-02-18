package eu.uwot.fabio.altcoinprices.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import eu.uwot.fabio.altcoinprices.utils.Coin;
import eu.uwot.fabio.altcoinprices.utils.CoinLogo;
import eu.uwot.fabio.altcoinprices.R;
import eu.uwot.fabio.altcoinprices.utils.UnixTimestamp;

public class EditPortfolioItemActivity extends AppCompatActivity {

    private String altcoinSymbol;
    private EditText amountBought_text;
    private float amountBought;
    private String altcoinCurrency;
    private EditText unitValue_text;
    private float unitValue;
    private Coin coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_portfolio_item);

        // extract data from bundle sent from PortfolioActivity
        TextView altcoinName_text = findViewById(R.id.altcoinSymbol);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            altcoinSymbol = b.getString("altcoinDescription");
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", 0);
            amountBought = prefs.getFloat(altcoinSymbol + "_a", 1f);
            unitValue = prefs.getFloat(altcoinSymbol + "_p", 1f);
            altcoinCurrency = prefs.getString(altcoinSymbol + "_currency", "EUR");
        }

        // set Altcoin name //
        coin = new Coin(getApplicationContext(), false);
        altcoinName_text.setText(coin.coinsLabelDescriptionHashtable.get(altcoinSymbol));

        // set Amount bought //
        amountBought_text = findViewById(R.id.amountBought_text);
        amountBought_text.setHint(Float.toString(amountBought));
        amountBought_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String getAmountBougt = amountBought_text.getText().toString();
                if (!getAmountBougt.equals("") && !getAmountBougt.equals(".")) {
                    amountBought = Float.parseFloat(getAmountBougt);
                } else {
                    amountBought = -1f;
                }
            }
        });

        // set Unit price //
        TextView altcoinCurrency_text = findViewById(R.id.tradingviewTradingPair);
        altcoinCurrency_text.append(" (" + altcoinCurrency + ")");

        unitValue_text = findViewById(R.id.unitPrice_text);
        unitValue_text.setHint(Float.toString(unitValue));

        unitValue_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String getUnitValue = unitValue_text.getText().toString();
                if (!getUnitValue.equals("") && !getUnitValue.equals(".")) {
                    unitValue = Float.parseFloat(getUnitValue);
                } else {
                    unitValue = -1f;
                }
            }
        });

        // edit date checkbox //
        final CheckBox editDateCheckbox = findViewById(R.id.editDateCheckbox);
        final View separator2 = findViewById(R.id.separator2);
        final View dateDescription = findViewById(R.id.dateDescription);
        final View datePicker = findViewById(R.id.datePicker);
        final View separator3 = findViewById(R.id.separator3);
        final View timeDescription = findViewById(R.id.timeDescription);
        final View timePicker = findViewById(R.id.timePicker);

        editDateCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editDateCheckbox.isChecked()) {
                    separator2.setVisibility(View.VISIBLE);
                    dateDescription.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.VISIBLE);
                    separator3.setVisibility(View.VISIBLE);
                    timeDescription.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                } else {
                    separator2.setVisibility(View.INVISIBLE);
                    dateDescription.setVisibility(View.INVISIBLE);
                    datePicker.setVisibility(View.INVISIBLE);
                    separator3.setVisibility(View.INVISIBLE);
                    timeDescription.setVisibility(View.INVISIBLE);
                    timePicker.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Save Button //
        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if ((amountBought != -1f) && (unitValue != -1f)) {
                    long unixTimestamp = 0L;

                    if (editDateCheckbox.isChecked()) {
                        DatePicker datePicker = findViewById(R.id.datePicker);
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();

                        TimePicker timePicker = findViewById(R.id.timePicker);
                        int hour, minute;
                        if(Build.VERSION.SDK_INT < 23){
                            hour = timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();
                        } else{
                            hour = timePicker.getHour();
                            minute = timePicker.getMinute();
                        }

                        unixTimestamp = new UnixTimestamp().getUnixTimestamp(minute, hour, day, month, year);
                    }

                    editItemThread(altcoinSymbol, amountBought, unitValue, unixTimestamp);
                }
            }
        });

        // Delete Button //
        final Button deleteButton = findViewById(R.id.removeButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            coin.removeItem(altcoinSymbol);
            new CoinLogo(getApplicationContext()).removeCustomCoinBitmapLogoFromStorage(altcoinSymbol);
            startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
            }
        });
    }


    private void editItemThread(final String altcoinSymbol, final float amountBought, final float unitPrice, final long unixTimestamp) {
        Thread editItemTh = new Thread() {
            public void run() {
                new Coin(getApplicationContext(), false).editItem(altcoinSymbol, amountBought, unitPrice, unixTimestamp);
                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
            }
        };

        editItemTh.start();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}