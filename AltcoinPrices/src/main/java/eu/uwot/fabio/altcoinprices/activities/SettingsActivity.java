package eu.uwot.fabio.altcoinprices.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import eu.uwot.fabio.altcoinprices.BuildConfig;
import eu.uwot.fabio.altcoinprices.utils.Coin;
import eu.uwot.fabio.altcoinprices.utils.CoinLogo;
import eu.uwot.fabio.altcoinprices.R;


public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Spinner currencySpinner;
    private String currency;
    private Spinner sortingSpinner;
    private String sorting;
    private final String DEFAULT_SORTING = "altcoinIndex";
    private Spinner periodSpinner;
    private String period;
    private Spinner timezoneSpinner;
    private String timezone;
    private final String DEFAULT_PERIOD = "60";
    private final String DEFAULT_TIMEZONE = "Etc/UTC";
    private CheckBox bollingerBandsCheckBox;
    private boolean bollingerBandsStatus;
    private CheckBox macdCheckBox;
    private boolean macdCheckBoxStatus;
    private CheckBox ichimokuCheckBox;
    private boolean ichimokuCheckBoxStatus;
    private CheckBox rsiCheckBox;
    private boolean rsiCheckBoxStatus;
    private String cryptocompareAPIString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // load  user settings //
        prefs = getApplicationContext().getSharedPreferences("Settings", 0);
        editor = prefs.edit();
        currency = prefs.getString("currency", "EUR");
        sorting = prefs.getString("sorting", DEFAULT_SORTING);
        this.period = prefs.getString("period", DEFAULT_PERIOD);
        this.timezone = prefs.getString("timezone", DEFAULT_TIMEZONE);
        this.bollingerBandsStatus = prefs.getBoolean("bollingerBandsStatus", false);
        this.macdCheckBoxStatus = prefs.getBoolean("macdCheckBoxStatus", false);
        this.ichimokuCheckBoxStatus = prefs.getBoolean("ichimokuCheckBoxStatus", false);
        this.rsiCheckBoxStatus = prefs.getBoolean("rsiCheckBoxStatus", false);
        this.cryptocompareAPIString = prefs.getString("cryptocompareAPIString", "");

        // Set currency //
        currencySpinner = findViewById(R.id.currencySpinner);
        // Create an ArrayAdapter using the string array and a default altcoinNameSpinner layout
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencyArray, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the currencyAdapter to the altcoinNameSpinner
        currencySpinner.setAdapter(currencyAdapter);
        currencySpinner.setOnItemSelectedListener(this);

        // Preload spinner with current currency setting
        int currencySpinnerPosition = currencyAdapter.getPosition(currency);
        currencySpinner.setSelection(currencySpinnerPosition);

        // Set sorting //
        sortingSpinner = findViewById(R.id.sortingSpinner);
        // Create an ArrayAdapter using the string array and a default altcoinNameSpinner layout
        ArrayAdapter<CharSequence> sortingAdapter = ArrayAdapter.createFromResource(this,
                R.array.sortingArray, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        sortingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the sortingAdapter to the altcoinNameSpinner
        sortingSpinner.setAdapter(sortingAdapter);
        sortingSpinner.setOnItemSelectedListener(this);

        // Preload spinner with sorting period setting
        String sortingspinnerLabel = sortingToLabel(sorting);
        int sortingSpinnerPosition = sortingAdapter.getPosition(sortingspinnerLabel);
        sortingSpinner.setSelection(sortingSpinnerPosition);

        // Set candlestick period //
        periodSpinner = findViewById(R.id.periodSpinner);
        // Create an ArrayAdapter using the string array and a default altcoinNameSpinner layout
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(this,
                R.array.periodArray, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the periodAdapter to the altcoinNameSpinner
        periodSpinner.setAdapter(periodAdapter);
        periodSpinner.setOnItemSelectedListener(this);

        // Preload spinner with current period setting
        String periodspinnerLabel = periodToLabel(period);
        int periodSpinnerPosition = periodAdapter.getPosition(periodspinnerLabel);
        periodSpinner.setSelection(periodSpinnerPosition);

        // Set timezone //
        timezoneSpinner = findViewById(R.id.timezoneSpinner);
        // Create an ArrayAdapter using the string array and a default altcoinNameSpinner layout
        ArrayAdapter<CharSequence> timezoneAdapter = ArrayAdapter.createFromResource(this,
                R.array.timezoneArray, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        timezoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the timezoneAdapter to the altcoinNameSpinner
        timezoneSpinner.setAdapter(timezoneAdapter);
        timezoneSpinner.setOnItemSelectedListener(this);

        // Timezone spinner with current period setting
        String timezoneSpinnerLabel = timezoneToLabel(timezone);
        int timezoneSpinnerPosition = timezoneAdapter.getPosition(timezoneSpinnerLabel);
        timezoneSpinner.setSelection(timezoneSpinnerPosition);

        // Select indicators //
        bollingerBandsCheckBox = findViewById(R.id.bollingerBandsCheckBox);
        bollingerBandsCheckBox.setChecked(bollingerBandsStatus);
        bollingerBandsCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bollingerBandsCheckBox.isChecked()) {
                    editor.putBoolean("bollingerBandsStatus", true);
                } else {
                    editor.putBoolean("bollingerBandsStatus", false);
                }

                editor.apply();
            }
        });
        macdCheckBox = findViewById(R.id.macdCheckBox);
        macdCheckBox.setChecked(macdCheckBoxStatus);
        macdCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (macdCheckBox.isChecked()) {
                    editor.putBoolean("macdCheckBoxStatus", true);
                } else {
                    editor.putBoolean("macdCheckBoxStatus", false);
                }

                editor.apply();
            }
        });
        ichimokuCheckBox = findViewById(R.id.ichimokuCheckBox);
        ichimokuCheckBox.setChecked(ichimokuCheckBoxStatus);
        ichimokuCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ichimokuCheckBox.isChecked()) {
                    editor.putBoolean("ichimokuCheckBoxStatus", true);
                } else {
                    editor.putBoolean("ichimokuCheckBoxStatus", false);
                }

                editor.apply();
            }
        });
        rsiCheckBox = findViewById(R.id.rsiCheckBox);
        rsiCheckBox.setChecked(rsiCheckBoxStatus);
        rsiCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rsiCheckBox.isChecked()) {
                    editor.putBoolean("rsiCheckBoxStatus", true);
                } else {
                    editor.putBoolean("rsiCheckBoxStatus", false);
                }

                editor.apply();
            }
        });

        // CryptoCompare API key //
        TextView cryptocompareAPI = findViewById(R.id.cryptocompareAPI);
        cryptocompareAPI.setMovementMethod(LinkMovementMethod.getInstance());

        final EditText cryptocompareAPI_text = findViewById(R.id.cryptocompareAPI_text);
        cryptocompareAPI_text.setHint("API key");
        cryptocompareAPI_text.setText(cryptocompareAPIString, TextView.BufferType.EDITABLE);
        cryptocompareAPI_text.addTextChangedListener(new TextWatcher() {

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
                editor.putString("cryptocompareAPIString", cryptocompareAPI_text.getText().toString());
                editor.apply();
            }
        });

        // ImportSettings - open file picker and select file to import data from //
        TextView importSettings = findViewById(R.id.importSettings);
        importSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    // Request permission from the user
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                } else {
                    if (importSharedPreferenceFromFile()) {
                        Toast.makeText(getApplicationContext(), R.string.settingsImported, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // ExportSettings - save settings in /sdcard //
        TextView exportSettings = findViewById(R.id.exportSettings);
        exportSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    // Request permission from the user
                    ActivityCompat.requestPermissions(SettingsActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    editor.putBoolean("initCoinsLogos", true);
                    editor.remove("status");
                    editor.apply();
                    if (exportSharedPreferenceToFile()) {
                        Toast.makeText(getApplicationContext(), R.string.settingsExported, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Reload coins logos //
        TextView reloadCoinsLogos = findViewById(R.id.reloadCoinsLogos);
        reloadCoinsLogos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Thread retrieveData = new Thread() {
                    public void run() {
                        Coin coin = new Coin(getApplicationContext(), false);
                        CoinLogo coinLogo = new CoinLogo(getApplicationContext());
                        coinLogo.removeCachedLogos();

                        for (int i = 0; i < coin.coins.length; i ++) {
                            float amountBought = prefs.getFloat(coin.coins[i] + "_a", -1f);

                            if (amountBought != -1f) {
                                coinLogo.getCoinLogo(coin.coins[i]);
                            }
                        }
                    }
                };

                retrieveData.start();
                Toast.makeText(getApplicationContext(), R.string.loadingCoinsLogos, Toast.LENGTH_SHORT).show();
            }
        });

        // About text - makes links clicable //
        TextView aboutText = findViewById(R.id.about);
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        aboutText.setText(R.string.aboutText);
        aboutText.append(" " + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (importSharedPreferenceFromFile()) {
                    Toast.makeText(getApplicationContext(), R.string.settingsImported, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (exportSharedPreferenceToFile()) {
                    Toast.makeText(getApplicationContext(), R.string.settingsExported, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean exportSharedPreferenceToFile() {
        ObjectOutputStream output = null;
        File path = new File("/storage/emulated/0/AltcoinPricesSettingsBackup.bin");

        try {
            output = new ObjectOutputStream(new FileOutputStream(path));
            SharedPreferences pref =
                    getSharedPreferences("Settings", MODE_PRIVATE);
            output.writeObject(pref.getAll());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    private boolean importSharedPreferenceFromFile() {
        ObjectInputStream input = null;
        File path = new File("/storage/emulated/0/AltcoinPricesSettingsBackup.bin");

        try {
            input = new ObjectInputStream(new FileInputStream(path));
            editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.clear();
            @SuppressWarnings("unchecked")
            Map<String, ?> entries = (Map<String, ?>) input.readObject();

            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    editor.putBoolean(key, (Boolean) v);
                else if (v instanceof Float)
                    editor.putFloat(key, (Float) v);
                else if (v instanceof String)
                    editor.putString(key, ((String) v));
            }
            editor.apply();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currency = currencySpinner.getSelectedItem().toString();
        editor.putString("currency", currency);
        editor.apply();

        sorting = sortingSpinner.getSelectedItem().toString();

        if (getResources().getString(R.string.defaultSorting).equals(sorting)) {
            sorting = "altcoinIndex";
        } else if (getResources().getString(R.string.coinNameSorting).equals(sorting)) {
            sorting = "altcoinName";
        } else if (getResources().getString(R.string.coinCodeSorting).equals(sorting)) {
            sorting = "altcoinCode";
        } else if (getResources().getString(R.string.coinBalanceSorting).equals(sorting)) {
            sorting = "altcoinBalance";
        } else {
            sorting = DEFAULT_SORTING;
        }

        editor.putString("sorting", sorting);
        editor.apply();

        period = periodSpinner.getSelectedItem().toString();

        if ("1 minute".equals(period)) {
            period = "1";
        } else if ("3 minutes".equals(period)) {
            period = "3";
        } else if ("5 minutes".equals(period)) {
            period = "5";
        } else if ("15 minutes".equals(period)) {
            period = "15";
        } else if ("30 minutes".equals(period)) {
            period = "30";
        } else if (("1 " + getResources().getString(R.string.hour)).equals(period)) {
            period = "60";
        } else if (("2 " + getResources().getString(R.string.hours)).equals(period)) {
            period = "120";
        } else if (("3 " + getResources().getString(R.string.hours)).equals(period)) {
            period = "180";
        } else if (("4 " + getResources().getString(R.string.hours)).equals(period)) {
            period = "240";
        } else if (("1 " + getResources().getString(R.string.day)).equals(period)) {
            period = "D";
        } else if (("1 " + getResources().getString(R.string.week)).equals(period)) {
            period = "W";
        } else if (("1 " + getResources().getString(R.string.month)).equals(period)) {
            period = "M";
        } else {
            period = DEFAULT_PERIOD;
        }

        editor.putString("period", period);
        editor.apply();

        timezone = timezoneSpinner.getSelectedItem().toString();

        if ("UTC-10".equals(timezone)) {
            timezone = "Pacific/Honolulu";
        } else if ("UTC-7".equals(timezone)) {
            timezone = "America/Los_Angeles";
        } else if ("UTC-6".equals(timezone)) {
            timezone = "America/El_Salvador";
        } else if ("UTC-5".equals(timezone)) {
            timezone = "America/Bogota";
        } else if ("UTC-4".equals(timezone)) {
            timezone = "America/New_York";
        } else if ("UTC-3".equals(timezone)) {
            timezone = "America/Sao_Paulo";
        } else if ("UTC".equals(timezone)) {
            timezone = "UTC";
        } else if ("UTC+1".equals(timezone)) {
            timezone = "Europe/London";
        } else if ("UTC+2".equals(timezone)) {
            timezone = "Europe/Rome";
        } else if ("UTC+3".equals(timezone)) {
            timezone = "Europe/Athens";
        } else if ("UTC+4".equals(timezone)) {
            timezone = "Asia/Dubai";
        } else if ("UTC+5".equals(timezone)) {
            timezone = "Asia/Ashkhabad";
        } else if ("UTC+6".equals(timezone)) {
            timezone = "Asia/Almaty";
        } else if ("UTC+7".equals(timezone)) {
            timezone = "Asia/Bangkok";
        } else if ("UTC+8".equals(timezone)) {
            timezone = "Asia/Hond_Kong";
        } else if ("UTC+9".equals(timezone)) {
            timezone = "Asia/Tokyo";
        } else if ("UTC+10".equals(timezone)) {
            timezone = "Australia/Brisbane";
        } else if ("UTC+12".equals(timezone)) {
            timezone = "Pacific/Auckland";
        } else if ("UTC+13".equals(timezone)) {
            timezone = "Pacific/Fakaofo";
        } else {
            timezone = DEFAULT_TIMEZONE;
        }

        editor.putString("timezone", timezone);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    private String sortingToLabel(String sorting) {
        String label;

        switch (sorting) {
            case "altcoinIndex":
                label = getResources().getString(R.string.defaultSorting);
                break;
            case "altcoinName":
                label = getResources().getString(R.string.coinNameSorting);
                break;
            case "altcoinCode":
                label = getResources().getString(R.string.coinCodeSorting);
                break;
            case "altcoinBalance":
                label = getResources().getString(R.string.coinBalanceSorting);
                break;
            default:
                label = DEFAULT_SORTING;
                break;
        }

        return label;
    }

    private String periodToLabel(String period) {
        String label;

        switch (period) {
            case "1":
                label = "1 minute";
                break;
            case "3":
                label = "3 minutes";
                break;
            case "5":
                label = "5 minutes";
                break;
            case "15":
                label = "15 minutes";
                break;
            case "30":
                label = "30 minutes";
                break;
            case "60":
                label = "1 " + getResources().getString(R.string.hour);
                break;
            case "120":
                label = "2 " + getResources().getString(R.string.hours);
                break;
            case "180":
                label = "3 " + getResources().getString(R.string.hours);
                break;
            case "240":
                label = "4 " + getResources().getString(R.string.hours);
                break;
            case "D":
                label = "1 " + getResources().getString(R.string.day);
                break;
            case "W":
                label = "1 " + getResources().getString(R.string.week);
                break;
            case "M":
                label = "1 " + getResources().getString(R.string.month);
                break;
            default:
                label = DEFAULT_PERIOD;
                break;
        }

        return label;
    }

    private String timezoneToLabel(String timezone) {
        String label;

        if ("Pacific/Honolulu".equals(timezone)) {
            label = "UTC-10";
        } else if ("America/Los_Angeles".equals(timezone)) {
            label = "UTC-7";
        } else if ("America/El_Salvador".equals(timezone)) {
            label = "UTC-6";
        } else if ("America/Bogota".equals(timezone)) {
            label = "UTC-5";
        } else if ("America/New_York".equals(timezone)) {
            label = "UTC-4";
        } else if ("America/Sao_Paulo".equals(timezone)) {
            label = "UTC-3";
        } else if ("UTC".equals(timezone)) {
            label = "UTC";
        } else if ("Europe/London".equals(timezone)) {
            label = "UTC+1";
        } else if ("Europe/Rome".equals(timezone)) {
            label = "UTC+2";
        } else if ("Europe/Athens".equals(timezone)) {
            label = "UTC+3";
        } else if ("Asia/Dubai".equals(timezone)) {
            label = "UTC+4";
        } else if ("Asia/Ashkhabad".equals(timezone)) {
            label = "UTC+5";
        } else if ("Asia/Almaty".equals(timezone)) {
            label = "UTC+6";
        } else if ("Asia/Bangkok".equals(timezone)) {
            label = "UTC+7";
        } else if ("Asia/Hond_Kong".equals(timezone)) {
            label = "UTC+8";
        } else if ("Asia/Tokyo".equals(timezone)) {
            label = "UTC+9";
        } else if ("Australia/Brisbane".equals(timezone)) {
            label = "UTC+10";
        } else if ("Pacific/Auckland".equals(timezone)) {
            label = "UTC+12";
        } else if ("Pacific/Fakaofo".equals(timezone)) {
            label = "UTC+13";
        } else {
            label = DEFAULT_TIMEZONE;
        }

        return label;
    }

}
