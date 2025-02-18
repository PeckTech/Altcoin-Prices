package eu.uwot.fabio.altcoinprices.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import eu.uwot.fabio.altcoinprices.R;


public class ManageCustomCoinsActivity extends AppCompatActivity {
    private ArrayList<CustomCoin> customCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_custom_coins);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCustomCoinActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        customCoins = new ArrayList<>();

        getCustomCoinsData();

        ListView listview = findViewById(R.id.customCoins);

        if (listview != null) {
            CustomCoinsListAdapter adapter = new CustomCoinsListAdapter(this, customCoins);
            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    Intent intent = new Intent(ManageCustomCoinsActivity.this, EditCustomCoinActivity.class);
                    String key = customCoins.get(position).altcoinSymbol;
                    Bundle b = new Bundle();
                    b.putString("key", key);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    // Load portfolio items array //
    private void getCustomCoinsData() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Settings", 0);

        String jsonData = prefs.getString("customCoins", "");

        assert jsonData != null;
        if (jsonData.length() != 0) {
            try {
                JSONObject json = new JSONObject(jsonData);
                Iterator<?> keys = json.keys();

                int y = 0;

                while (keys.hasNext()) {
                    String altcoinSymbol, altcoinDescription, tradingviewExchangeName;
                    String key = (String) keys.next();

                    JSONObject jsonCoinDetails = json.getJSONObject(key);
                    altcoinSymbol = jsonCoinDetails.getString("altcoinSymbol");
                    altcoinDescription = jsonCoinDetails.getString("altcoinDescription");
                    tradingviewExchangeName = jsonCoinDetails.getString("tradingviewExchangeName");

                    customCoins.add(y, new ManageCustomCoinsActivity.CustomCoin(
                            altcoinSymbol,
                            altcoinDescription,
                            tradingviewExchangeName));

                    y++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Display custom coins items list //
    class CustomCoin {
        final String altcoinSymbol;
        final String altcoinDescription;
        final String tradingviewExchangeName;

        CustomCoin(String altcoinSymbol, String altcoinDescription, String tradingviewExchangeName) {
            this.altcoinSymbol = altcoinSymbol;
            this.altcoinDescription = altcoinDescription;
            this.tradingviewExchangeName = tradingviewExchangeName;
        }
    }

    class CustomCoinsListAdapter extends ArrayAdapter<ManageCustomCoinsActivity.CustomCoin> {
        CustomCoinsListAdapter(Context context, ArrayList<ManageCustomCoinsActivity.CustomCoin> items) {
            super(context, R.layout.single_element_of_custom_coins, items);
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.single_element_of_custom_coins, null);
            }

            ManageCustomCoinsActivity.CustomCoin item = super.getItem(position);

            TextView altcoinSymbol = convertView.findViewById(R.id.altcoinSymbol);
            assert item != null;
            altcoinSymbol.setText(item.altcoinSymbol + " - ");
            TextView altcoinDescription = convertView.findViewById(R.id.altcoinDescription);
            altcoinDescription.setText(item.altcoinDescription);
            TextView tradingviewExchangeName = convertView.findViewById(R.id.tradingviewExchangeName);
            tradingviewExchangeName.setText(item.tradingviewExchangeName);

            return convertView;
        }
    }
}
