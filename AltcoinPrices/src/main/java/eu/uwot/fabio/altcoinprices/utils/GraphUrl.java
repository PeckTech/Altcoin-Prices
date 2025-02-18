package eu.uwot.fabio.altcoinprices.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GraphUrl {

    private final String URL0 = "<!-- TradingView Widget BEGIN -->\n" +
            "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>\n" +
            "<script type=\"text/javascript\">\n" +
            "new TradingView.widget({\n" +
            "  \"autosize\": true,\n" +
            "  \"symbol\": \"";
    private final String URL1 = "\",\n" +
            "  \"interval\": \"";
    private final String URL2 = "\",\n" +
            "  \"timezone\": \"";
    private final String URL3 = "\",\n" +
            "  \"theme\": \"Light\",\n" +
            "  \"style\": \"1\",\n" +
            "  \"locale\": \"en\",\n" +
            "  \"toolbar_bg\": \"#f1f3f6\",\n" +
            "  \"enable_publishing\": false,\n" +
            "  \"withdateranges\": true,\n" +
            "  \"save_image\": false,\n" +
            "  \"hideideas\": true,\n";
    private final String URL4 = "});\n" +
            "</script>\n" +
            "<!-- TradingView Widget END -->\n";

    private final Context context;
    private final SharedPreferences prefs;

    public GraphUrl(Context context) {
        this.context = context.getApplicationContext();
        prefs = context.getSharedPreferences("Settings", 0);
    }

    public String getUrl(String altcoinName) {
        Coin coin = new Coin(context, true);
        String period = prefs.getString("period", "30");
        String exchange = coin.getCoinExchange(altcoinName);
        String timezone = prefs.getString("timezone", "Etc/UTC");
        String indicators = getIndicators();

        return  URL0 +
                exchange +
                ":" +
                // altcoinName.toUpperCase() +
                coin.getTradingviewCoinSymbol(altcoinName.toUpperCase()) +
                coin.coinsLabelGraph.get(altcoinName) +
                URL1 +
                period +
                URL2 +
                timezone +
                URL3 +
                indicators +
                URL4;
    }

    private String getIndicators() {
        String res = "";

        boolean bollingerBandsStatus = prefs.getBoolean("bollingerBandsStatus", false);
        boolean macdCheckBoxStatus = prefs.getBoolean("macdCheckBoxStatus", false);
        boolean ichimokuCheckBoxStatus = prefs.getBoolean("ichimokuCheckBoxStatus", false);
        boolean rsiCheckBoxStatus = prefs.getBoolean("rsiCheckBoxStatus", false);

        if (bollingerBandsStatus) {
            res += "    \"BB@tv-basicstudies\",\n";
        }
        if (macdCheckBoxStatus) {
            res += "    \"MACD@tv-basicstudies\",\n";
        }
        if (ichimokuCheckBoxStatus) {
            res += "    \"IchimokuCloud@tv-basicstudies\",\n";
        }
        if (rsiCheckBoxStatus) {
            res += "    \"RSI@tv-basicstudies\",\n";
        }
        if (!res.equals("")) {
            res = "  \"studies\": [\n" + res + "\n],";
        }

        return res;
    }

}
