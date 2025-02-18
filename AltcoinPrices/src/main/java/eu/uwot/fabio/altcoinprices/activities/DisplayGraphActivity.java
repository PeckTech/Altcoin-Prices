package eu.uwot.fabio.altcoinprices.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import eu.uwot.fabio.altcoinprices.R;

public class DisplayGraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_graph);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            String url = b.getString("url");
            WebView view = this.findViewById(R.id.webView);
            view.getSettings().setJavaScriptEnabled(true);
            view.getSettings().setDomStorageEnabled(true);
            view.loadData(url, null, null);
        }
    }

}
