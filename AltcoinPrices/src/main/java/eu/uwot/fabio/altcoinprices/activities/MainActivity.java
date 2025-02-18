package eu.uwot.fabio.altcoinprices.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import eu.uwot.fabio.altcoinprices.utils.Coin;
import eu.uwot.fabio.altcoinprices.utils.CoinLogo;
import eu.uwot.fabio.altcoinprices.utils.GraphUrl;
import eu.uwot.fabio.altcoinprices.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String currency;
    private Coin coin;
    private boolean portfolioPeriodIsDay;
    private ArrayList<PortfolioItem> portfolioItems = new ArrayList<>();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private float currentPortfolioValue;

    // Activity's entry point, onCreateDo() is also used to refresh the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigationDrawerOpen, R.string.navigationDrawerClose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefs = getApplicationContext().getSharedPreferences("Settings", 0);
        editor = prefs.edit();
        currency = prefs.getString("currency", "EUR");
        portfolioPeriodIsDay = prefs.getBoolean("portfolioPeriodIsDay", false);
    }

    // Reload portfolio data when coming back from nested Activity //
    @Override
    public void onResume() {
        super.onResume();
        portfolioItems = new ArrayList<>();
        onCreateDo();
    }

    // Populate portfolio, sidebar, etc
    private void onCreateDo() {
        coin = new Coin(getApplicationContext(), true);

        // Load portfolio items array
        getPortfolioData();

        // add current balance in FIAT //
        TextView portfolioBalance = findViewById(R.id.portfolioBalance);
        currentPortfolioValue = getCurrentPortfolioValue();

        String decimalsToDisplay = getDecimals();

        portfolioBalance.setText(getCurrencySymbol(currency) +
                                 new DecimalFormat(decimalsToDisplay).format(currentPortfolioValue));
        // switch balance from FIAT to BTC and vice versa
        portfolioBalance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchPortfolioCurrency();
                onResume();
            }
        });

        // gain FIAT/Percentage //
        TextView portfolioGain = findViewById(R.id.portfolioGain);
        float currentPortfolioGain = getPortfolioGain(currentPortfolioValue);

        String sign;
        String currentPortfolioValueString;
        if (currentPortfolioGain >= 0) {
            portfolioGain.setTextColor(getResources().getColor(R.color.green));
            sign ="\u25B2"; //symbol: ▲
            currentPortfolioValueString = new DecimalFormat(decimalsToDisplay).format(getPortfolioGainFiat(currentPortfolioValue));
        } else {
            portfolioGain.setTextColor(getResources().getColor(R.color.red));
            sign ="\u25BC -"; //symbol: ▼
            currentPortfolioValueString = new DecimalFormat(decimalsToDisplay).format(getPortfolioGainFiat(currentPortfolioValue) * -1f);
        }

        portfolioGain.setText(sign +
                              getCurrencySymbol(currency) +
                              currentPortfolioValueString +
                              " (" +
                              new DecimalFormat("#.##").format(currentPortfolioGain) +
                              "%)" );
        // switch balance from FIAT to BTC and vice versa
        portfolioGain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switchPortfolioCurrency();
                onResume();
            }
        });

        // portfolio period selector //
        final TextView portfolioPeriod = findViewById(R.id.portfolioPeriod);

        if (portfolioPeriodIsDay) {
            String text = "<font color='#777777'>" +
                    getResources().getString(R.string.all_time) +
                    "</font> <font color='#777777'>◀</font> <font color='#f18400'>" +
                    getResources().getString(R.string.today) +
                    "</font>";
            portfolioPeriod.setText(Html.fromHtml(text));
        } else {
            String text = "<font color='#f18400'>" +
                    getResources().getString(R.string.all_time) +
                    "</font> <font color='#777777'>▶</font> <font color='#777777'>" +
                    getResources().getString(R.string.today) +
                    "</font>";
            portfolioPeriod.setText(Html.fromHtml(text));
        }

        portfolioPeriod.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (portfolioPeriodIsDay) {
                    portfolioPeriodIsDay = false;
                    editor.putBoolean("portfolioPeriodIsDay", false);
                } else {
                    portfolioPeriodIsDay = true;
                    editor.putBoolean("portfolioPeriodIsDay", true);
                }

                editor.apply();

                onResume();
            }
        });


        // display altcoin in portfolio //
        ListView listview = findViewById(R.id.portfolioItems);

        if (listview != null) {
            PortfolioAdapter adapter = new PortfolioAdapter(this, portfolioItems);
            listview.setAdapter(adapter);

            // Open WebView and display graph
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (getNetworkStateAvailable()) {
                        String altcoinName = portfolioItems.get(position).altcoinName;

                        if (!coin.coinsLabelGraph.get(altcoinName).equals("na")) {
                            loadGraph(altcoinName);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.graphUnavailable, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.networkUnavailable, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Edit Portfolio item
            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String altcoinName = portfolioItems.get(position).altcoinName;

                    Intent intent = new Intent(MainActivity.this, EditPortfolioItemActivity.class);
                    Bundle b = new Bundle();
                    b.putString("altcoinDescription", altcoinName);
                    intent.putExtras(b);
                    startActivity(intent);

                    return true;
                }
            });
        }

        // Populate left menu with coins
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();

        for (int i = 0; i < coin.coinsLabelDescriptionsString.length; i++) {
            if (!coin.coinsLabelGraph.get(coin.coins[i]).equals("na")) {
                String imageResource = "ic_menu_" + coin.coins[i].toLowerCase();
                int id = getResources().getIdentifier("eu.uwot.fabio.altcoinprices:drawable/" +
                        imageResource, null, null);
                menu.add(coin.coinsLabelDescriptionsString[i]).setIcon(id);
            }
        }

        // Show Toast if no CryptoCompare API key is missing
        boolean apiError = prefs.getBoolean("apiError", false);
        if (apiError) {
            Toast.makeText(getApplicationContext(), R.string.missingAPIkeyError, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_manageCustomCoins) {
            startActivity(new Intent(this, ManageCustomCoinsActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_addNew) {
            startActivity(new Intent(this, AddNewPortfolioItemActivity.class));
            return true;
        } else if (id == R.id.action_reload) {
            startActivity(new Intent(this, LoadingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Open the DisplayGraphActivity when menu item is clicked //
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (getNetworkStateAvailable()) {
            String description = (String) item.getTitle();
            String altcoinName = coin.coinsDescriptionLabelHashtable.get(description);
            loadGraph(altcoinName);

            return true;
        } else {
            Toast.makeText(getApplicationContext(), R.string.networkUnavailable, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void switchPortfolioCurrency() {
        String currencyFromSettings = prefs.getString("currency", "EUR");

        if (currency.equals("BTC")) {
            if (currencyFromSettings.equals("BTC")) {
                currency = "USD";
            } else {
                currency = currencyFromSettings;
            }
        } else if (currency.equals(currencyFromSettings)) {
            currency = "BTC";
        } else {
            currency = currencyFromSettings;
        }
    }

    private void loadGraph(String altcoinName) {
        GraphUrl graphUrl = new GraphUrl(getApplicationContext());
        String url = graphUrl.getUrl(altcoinName);

        Intent intent = new Intent(MainActivity.this, DisplayGraphActivity.class);
        Bundle b = new Bundle();
        b.putString("url", url);
        intent.putExtras(b);
        startActivity(intent);
    }

    private String getCurrencySymbol(String currency) {
        String currencySymbol = null;

        switch (currency) {
            case "EUR":
                currencySymbol = "€";
                break;
            case "USD":
                currencySymbol = "$";
                break;
            case "GBP":
                currencySymbol = "£";
                break;
            case "CNY":
                currencySymbol = "元";
                break;
            case "JPY":
                currencySymbol = "JP¥";
                break;
            case "RUB":
                currencySymbol = "\u20BD";
                break;
            case "CAD":
                currencySymbol = "C$";
                break;
            case "AUD":
                currencySymbol = "AUD";
                break;
            case "INR":
                currencySymbol = "₹";
                break;
            case "KRW":
                currencySymbol = "₩";
                break;
            case "CHF":
                currencySymbol = "CHF";
                break;
            case "BTC":
                if (Build.VERSION.SDK_INT >= 26) {
                    currencySymbol = "\u20BF";
                } else {
                    currencySymbol = "BTC";
                }
                break;
        }

        return currencySymbol;
    }

    // Load portfolio items array //
    private void getPortfolioData() {
        String coinName;
        int coinIndex;
        float amountBought;
        float unitValue;
        float lastDayUnitValue;
        float currentUnitValue;
        String currency;

        int y = 0;

        for (int i = 0; i < coin.coins.length; i ++) {
            amountBought = prefs.getFloat(coin.coins[i] + "_a", -1f);

            if (amountBought != -1f) {
                coinName = coin.coins[i];
                coinIndex = i;
                if (this.currency.equals("BTC")) {
                    unitValue = prefs.getFloat(coin.coins[i] + "_p_btc", -1f);
                    currentUnitValue = prefs.getFloat(coin.coins[i] + "_currentUnitValue_btc", -1f);
                    lastDayUnitValue = prefs.getFloat(coin.coins[i] + "_lastDayUnitValue_btc", -1f);
                } else {
                    unitValue = prefs.getFloat(coin.coins[i] + "_p", -1f);
                    currentUnitValue = prefs.getFloat(coin.coins[i] + "_currentUnitValue", -1f);
                    lastDayUnitValue = prefs.getFloat(coin.coins[i] + "_lastDayUnitValue", -1f);
                }
                currency = prefs.getString(coin.coins[i] + "_currency", null);

                portfolioItems.add(y, new PortfolioItem(coinName,
                        coinIndex,
                        amountBought,
                        unitValue,
                        lastDayUnitValue,
                        currentUnitValue,
                        currency));

                y ++;
            }
        }

        // Sort portfolio items according to user selected key
        Collections.sort(portfolioItems, new Comparator<PortfolioItem>() {
            final String sorting = prefs.getString("sorting", "altcoinIndex");
            @Override
            public int compare(PortfolioItem altcoin1, PortfolioItem altcoin2) {
                assert sorting != null;
                switch (sorting) {
                    case "altcoinName":
                        return coin.descriptions[altcoin1.altcoinIndex].compareTo(coin.descriptions[altcoin2.altcoinIndex]);
                    case "altcoinCode":
                        return altcoin1.altcoinName.compareTo(altcoin2.altcoinName);
                    case "altcoinBalance":
                        return -1 * Float.compare(altcoin1.currentUnitValue * altcoin1.amountBought, altcoin2.currentUnitValue * altcoin2.amountBought);
                    default:   // Default: altcoinIndex
                        if (altcoin1.altcoinIndex > altcoin2.altcoinIndex) {
                            return 1;
                        } else if (altcoin1.altcoinIndex < altcoin2.altcoinIndex) {
                            return -1;
                        } else {
                            return 0;
                        }
                }
            }
        });

    }

    private float getCurrentPortfolioValue() {
        float amountBought;
        float currentCoinValue;
        float total = 0f;

        for (int i = 0; i < coin.coins.length; i ++) {
            amountBought = prefs.getFloat(coin.coins[i] + "_a", 0f);

            if (amountBought > 0) {
                if (currency.equals("BTC")) {
                    currentCoinValue = prefs.getFloat(coin.coins[i] + "_currentUnitValue_btc", 0f);
                } else {
                    String altcoinCurrency = prefs.getString(coin.coins[i] + "_currency", "EUR");
                    currentCoinValue = prefs.getFloat(coin.coins[i] + "_currentUnitValue", 0f);

                    if (!altcoinCurrency.equals(currency)) {
                        currentCoinValue = coin.currencyToCurrency(currentCoinValue, altcoinCurrency, currency);
                    }
                }

                total += amountBought * currentCoinValue;
            }
        }

        return total;
    }

    private float getPortfolioGainFiat(float currentPortfolioValue) {
        float initialValue = 0f;

        for (int i = 0; i < coin.coins.length; i ++) {
            float amountBought = prefs.getFloat(coin.coins[i] + "_a", 0);

            if (amountBought > 0) {
                //float unitPrice = prefs.getFloat(coin.coins[i] + "_p", 0);
                float unitPrice;

                if (portfolioPeriodIsDay) {
                    if (currency.equals("BTC")) {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_lastDayUnitValue_btc", 0);
                    } else {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_lastDayUnitValue", 0);
                    }
                } else {
                    if (currency.equals("BTC")) {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_p_btc", 0);

                        if (unitPrice == -1f) {
                            unitPrice = 0f;
                        }
                    } else {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_p", 0);
                    }
                }

                String altcoinCurrency = prefs.getString(coin.coins[i] + "_currency", "EUR");

                if ((!altcoinCurrency.equals(currency)) && (!currency.equals("BTC"))) {
                    unitPrice = coin.currencyToCurrency(unitPrice, altcoinCurrency, currency);
                }

                initialValue += amountBought * unitPrice;
            }
        }

        return currentPortfolioValue - initialValue;
    }

    private float getPortfolioGain(float currentPortfolioValue) {
        float initialValue = 0f;

        for (int i = 0; i < coin.coins.length; i ++) {
            float amountBought = prefs.getFloat(coin.coins[i] + "_a", 0);

            if (amountBought > 0) {
                float unitPrice;

                if (portfolioPeriodIsDay) {
                    if (currency.equals("BTC")) {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_lastDayUnitValue_btc", 0);
                    } else {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_lastDayUnitValue", 0);
                    }
                } else {
                    if (currency.equals("BTC")) {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_p_btc", 0);

                        if (unitPrice == -1f) {
                            unitPrice = 0f;
                        }
                    } else {
                        unitPrice = prefs.getFloat(coin.coins[i] + "_p", 0);
                    }
                }

                String altcoinCurrency = prefs.getString(coin.coins[i] + "_currency", "EUR");

                if ((!altcoinCurrency.equals(currency)) && (!currency.equals("BTC"))) {
                    unitPrice = coin.currencyToCurrency(unitPrice, altcoinCurrency, currency);
                }

                initialValue += amountBought * unitPrice;
            }
        }

        float gain = (currentPortfolioValue - initialValue) / initialValue * 100;

        // Check that gain is a valid number //
        if (gain != gain) {
            return 0f;
        } else {
            return gain;
        }
    }

    // Get decimals to display //
    private String getDecimals() {
        if (currency.equals("BTC")) {
            return "#.########";
        } else {
            return "###,###.###";
        }
    }

    // Get network state
    private boolean getNetworkStateAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Display portfolio items list //
    class PortfolioItem {
        final String altcoinName;
        final int altcoinIndex;
        final float amountBought;
        final float unitValue;
        final float lastDayUnitValue;
        final float currentUnitValue;
        final String currency;

        PortfolioItem(String altcoinName, int altcoinIndex, float amountBought, float unitValue, float lastDayUnitValue, float currentUnitValue, String currency) {
            this.altcoinName = altcoinName;
            this.altcoinIndex = altcoinIndex;
            this.amountBought = amountBought;
            this.unitValue = unitValue;
            this.lastDayUnitValue = lastDayUnitValue;
            this.currentUnitValue = currentUnitValue;
            this.currency = currency;
        }
    }

    class PortfolioAdapter extends ArrayAdapter<PortfolioItem> {
        PortfolioAdapter(Context context, ArrayList<PortfolioItem> items) {
            super(context, R.layout.single_element_of_portfolio, items);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.single_element_of_portfolio, null);
            }

            PortfolioItem item = super.getItem(position);
            String decimalsToDisplay = getDecimals();

            // Check which currency was used when altcoin was added the first time //
            assert item != null;
            String currencySymbol;
            if (currency.equals("BTC")) {
                currencySymbol = getCurrencySymbol("BTC");
            } else {
                currencySymbol = getCurrencySymbol(item.currency);
            }

            // Portfolio Altcoin Icon //
            ImageView imageView = convertView.findViewById(R.id.portfolioAltcoinLogo);
            int size = getPortfolioItemLogoImageSize();
            Bitmap coinLogo = new CoinLogo(getApplicationContext()).loadCustomCoinBitmapLogoFromStorage(item.altcoinName);
            if (coinLogo != null) {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(
                        coinLogo,
                        size,
                        size,
                        true));
            }

            // Portfolio Altcoin Name //
            TextView portfolioAltcoinName = convertView.findViewById(R.id.portfolioAltcoinName);

            if (portfolioAltcoinName != null) {
                portfolioAltcoinName.setText(coin.coinsLabelDescriptionHashtable.get(item.altcoinName));
            }

            // Portfolio Altcoin Balance //
            float altcoinBalance = item.currentUnitValue * item.amountBought;
            TextView portfolioAltcoinBalance = convertView.findViewById(R.id.portfolioAltcoinBalance);

            if (portfolioAltcoinBalance != null) {
                portfolioAltcoinBalance.setText(item.amountBought +
                        " " +
                        item.altcoinName +
                        " (" +
                        currencySymbol +
                        new DecimalFormat(decimalsToDisplay).format(altcoinBalance) +
                        ")");

                portfolioAltcoinBalance.setTextColor(getResources().getColor(R.color.colorTextMain));
            }

            // Portfolio Altcoin Gain //
            float investmentValue = 0f;
            float gain = 0f;

            if (portfolioPeriodIsDay) {
                if (altcoinBalance > 0f) {
                    investmentValue = item.lastDayUnitValue * item.amountBought;
                    gain = (altcoinBalance - investmentValue) / investmentValue * 100;
                }
            } else {
                if (altcoinBalance > 0f) {
                    investmentValue = item.unitValue * item.amountBought;
                    gain = (altcoinBalance - investmentValue) / investmentValue * 100;
                }
            }

            float gainFiat = altcoinBalance - investmentValue;
            String gainFiatString;

            TextView portfolioAltcoinGain = convertView.findViewById(R.id.portfolioAltcoinGain);
            if (portfolioAltcoinGain != null) {
                String sign;

                if (gainFiat >= 0f) {
                    sign ="\u25B2"; //symbol: ▲
                    portfolioAltcoinGain.setTextColor(getResources().getColor(R.color.green));
                    gainFiatString = new DecimalFormat(decimalsToDisplay).format(gainFiat);
                } else {
                    sign ="\u25BC -"; //symbol: ▼
                    portfolioAltcoinGain.setTextColor(getResources().getColor(R.color.red));
                    gainFiatString = new DecimalFormat(decimalsToDisplay).format(gainFiat * -1f);
                }

                portfolioAltcoinGain.setText(sign +
                        currencySymbol +
                        gainFiatString +
                        " (" +
                        new DecimalFormat("#.##").format(gain) +
                        "%)");
            }

            // Portfolio Altcoin Weight //
            if (!currency.equals("BTC")) {
                altcoinBalance = coin.currencyToCurrency(altcoinBalance, item.currency, currency);
            }

            float altcoinWeight = altcoinBalance / currentPortfolioValue * 100;
            TextView portfolioAltcoinWeight = convertView.findViewById(R.id.portfolioAltcoinWeight);
            if (altcoinBalance == currentPortfolioValue) {
                portfolioAltcoinWeight.setText(getResources().getString(R.string.weight) +
                        ": 100.0%");
            } else {
                portfolioAltcoinWeight.setText(getResources().getString(R.string.weight) +
                        ": " +
                        new DecimalFormat("0.0").format(altcoinWeight) +
                        "%");
            }

            // Portfolio Altcoin Unit Value //
            TextView portfolioAltcoinUnitValue = convertView.findViewById(R.id.portfolioAltcoinUnitValue);
            if (portfolioAltcoinUnitValue != null) {
                portfolioAltcoinUnitValue.setText("1" + item.altcoinName +
                        " = " +
                        currencySymbol +
                        new DecimalFormat(decimalsToDisplay).format(item.currentUnitValue));
                portfolioAltcoinUnitValue.setTextColor(getResources().getColor(R.color.colorTextMain));
            }

            return convertView;
        }

        private int getPortfolioItemLogoImageSize() {
            float density = getResources().getDisplayMetrics().density;

            if (density >= 4.0) {           // xxxhdpi
                return 96;
            } else if (density >= 3.0) {    // xxhdpi
                return 96;
            } else if (density >= 2.0) {    // xhdpi
                return 64;
            } else if (density >= 1.5) {    // hdpi
                return 48;
            } else if (density >= 1.0) {    // mdpi
                return 32;
            } else {                        // ldpi
                return 16;
            }
        }
    }

}
