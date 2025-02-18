package eu.uwot.fabio.altcoinprices.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import eu.uwot.fabio.altcoinprices.utils.Coin;
import eu.uwot.fabio.altcoinprices.R;
import eu.uwot.fabio.altcoinprices.utils.UnixTimestamp;

public class AddNewPortfolioItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String altcoinDescription;
    private EditText amountBought_text;
    private float amountBought;
    private EditText unitPrice_text;
    private float unitPrice;
    private Spinner altcoinNameSpinner;
    private Spinner currencySpinner;
    private String currency;
    private Coin coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_portfolio_item);

        // set Altcoin Description //
        altcoinNameSpinner = findViewById(R.id.altcoinName_spinner);

        coin = new Coin(getApplicationContext(), true);
        ArrayAdapter<String> adapter_altcoin = new ArrayAdapter<>(this,
                R.layout.spinner_item, coin.coinsLabelDescriptionsString);

        // Specify the layout to use when the list of choices appears
        adapter_altcoin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_altcoin to the altcoinNameSpinner
        altcoinNameSpinner.setAdapter(adapter_altcoin);

        altcoinNameSpinner.setOnItemSelectedListener(this);

        // set Amount bought //
        amountBought_text = findViewById(R.id.amountBought_text);

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
                String getAmountBought = amountBought_text.getText().toString();
                if (!getAmountBought.equals("") && !getAmountBought.equals(".")) {
                    amountBought = Float.parseFloat(getAmountBought);
                } else {
                    amountBought = -1f;
                }
            }
        });

        // set Unit price //
        unitPrice_text = findViewById(R.id.unitPrice_text);

        unitPrice_text.addTextChangedListener(new TextWatcher() {

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
                String getUnitPrice = unitPrice_text.getText().toString();
                if (!getUnitPrice.equals("") && !getUnitPrice.equals(".")) {
                    unitPrice = Float.parseFloat(getUnitPrice);
                } else {
                    unitPrice = -1f;
                }
            }
        });

        // set trading pair //
        currencySpinner = findViewById(R.id.currency_spinner);
        // Create an ArrayAdapter using the string array and a default currencySpinner layout
        ArrayAdapter<CharSequence> adapter_currency = ArrayAdapter.createFromResource(this,
                R.array.currencyArray, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_currency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter_currency to the currencySpinner
        currencySpinner.setAdapter(adapter_currency);

        currencySpinner.setOnItemSelectedListener(this);

        // Save Button //
        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check that both amountBought and unitPrice are >= 0
                if ((amountBought >= 0f) && (unitPrice >= 0f)) {
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

                    long unixTimestamp = new UnixTimestamp().getUnixTimestamp(minute, hour, day, month, year);

                    addItemThread(altcoinDescription, amountBought, unitPrice, currency, unixTimestamp);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.formIsIncomplete, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back Button //
        final Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        altcoinDescription = altcoinNameSpinner.getSelectedItem().toString();
        currency = currencySpinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    private void addItemThread(final String altcoinDesc, final float amountBought, final float unitPrice, final String currency, final long unixTimestamp) {
        Thread addItemTh = new Thread() {
            public void run() {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", 0);
                SharedPreferences.Editor editor = prefs.edit();
                new Coin(getApplicationContext(), true).addItem(altcoinDesc, amountBought, unitPrice, currency, unixTimestamp);
                editor.putBoolean("initCoinsLogos", true);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
            }
        };

        addItemTh.start();
    }

}
