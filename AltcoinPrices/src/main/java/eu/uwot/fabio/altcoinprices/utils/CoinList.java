package eu.uwot.fabio.altcoinprices.utils;

import java.util.Hashtable;

abstract class CoinList {
    public static final String[] coins = new String[] {
            "BTC",
            "BCH",
            "ETH",
            "LTC",

            "AE",
            "ATOM",
            "REP",
            "BAT",
            "BNB",
            "BCD",
            "BSV",
            "BTG",
            "BTS",
            "BLK",
            "BCN",
            "BTM",
            "ADA",
            "LINK",
            "DASH",
            "DCR",
            "DGD",
            "DOGE",
            "EOS",
            "ETC",
            "GNO",
            "GNT",
            "HSR",
            "KIN",
            "ICX",
            "ICN",
            "IOST",
            "MIOTA",
            "LSK",
            "LRC",
            "MLN",
            "XMR",
            "NANO",
            "XEM",
            "NEO",
            "OMG",
            "ONT",
            "PART",
            "PPT",
            "QTUM",
            "RHOC",
            "REQ",
            "XRP",
            "SC",
            "SNT",
            "STEEM",
            "XLM",
            "STRAT",
            "TRTL",
            "TRX",
            "VEN",
            "XTZ",
            "XVG",
            "VRM",
            "WTC",
            "WAN",
            "WAVES",
            "ZEC",
            "ZIL",
            "ZRX",

            "BLX",
            "CCC",
            "SOPR",
            "FCI",
            "GEM",
            "PCC",
            "WMX",
            "BMC",
            "AAAX",
            "CAR",
            "KCOR",
            "CBST",
            "RCAA",
            "BIF",
            "CRNC",
            "BGA15",
            "BEA",
            "BLS",
            "FCE",
            "PPI",
            "JJK",
            "TRADE",
            "EAA",
            "SCX",
            "CCP"
    };
    public static final String[] iconomi_daa = new String[]{
            "BLX",
            "CCC",
            "SOPR",
            "FCI",
            "GEM",
            "PCC",
            "WMX",
            "BMC",
            "AAAX",
            "CAR",
            "KCOR",
            "CBST",
            "RCAA",
            "BIF",
            "CRNC",
            "BGA15",
            "BEA",
            "BLS",
            "FCE",
            "PPI",
            "JJK",
            "TRADE",
            "EAA",
            "SCX",
            "CCP"
    };
    public static final String[] descriptions = new String[]{
            "Bitcoin",
            "Bitcoin Cash",
            "Ethereum",
            "Litecoin",

            "Aeternity",
            "Cosmos",
            "Augur",
            "Basic Attention Token",
            "Binance Coin",
            "Bitcoin Diamond",
            "Bitcoin SV",
            "Bitcoin Gold",
            "Bitshares",
            "BlackCoin",
            "Bytecoin",
            "Bytom",
            "Cardano",
            "ChainLink",
            "Dash",
            "Decred",
            "Digix DAO",
            "Dogecoin",
            "EOS",
            "Ethereum Classic",
            "Gnosis",
            "Golem",
            "Hshare",
            "Kin",
            "Icon",
            "Iconomi",
            "IOStoken",
            "IOTA",
            "Lisk",
            "Loopring",
            "Melon",
            "Monero",
            "Nano",
            "NEM",
            "NEO",
            "OmiseGO",
            "Ontology",
            "Particl",
            "Populous",
            "Qtum",
            "RChain",
            "Request Network",
            "Ripple",
            "Siacoin",
            "Status Network Token",
            "Steem",
            "Stellar Lumens",
            "Stratis",
            "TurtleCoin",
            "TRON",
            "VeChain",
            "Tezos",
            "Verge",
            "Verium",
            "Waltonchain",
            "Wanchain",
            "Waves",
            "Zcash",
            "Zilliqa",
            "0x",

            "Blockchain Index (ICN DAA)",
            "Crush Crypto Core (ICN DAA)",
            "Solidum Prime (ICN DAA)",
            "Future Chain Index (ICN DAA)",
            "Greychain Emerging Markets (ICN DAA)",
            "The Pecunio Cryptocurrency (ICN DAA)",
            "William Mougayar High Growth Cryptoassets Index (ICN DAA)",
            "BMC Original (ICN DAA)",
            "The Asymmetry DAA (ICN DAA)",
            "CARUS-AR (ICN DAA)",
            "KryptoStar CORE (ICN DAA)",
            "Coinbest 1 (ICN DAA)",
            "Ragnarok Crypto Asset Array (ICN DAA)",
            "Blockchain Infrastructure Index (ICN DAA)",
            "Cornucopia Index (ICN DAA)",
            "Global Blockchain Arrays represents (ICN DAA)",
            "Blockchain Easy Access (ICN DAA)",
            "Blockchain Smart (ICN DAA)",
            "Future Crypto Economy (ICN DAA)",
            "Phoenix Paradigm Indicator (ICN DAA)",
            "JJK Crypto Assets (ICN DAA)",
            "Trade (ICN DAA)",
            "Exponential Age Array (ICN DAA)",
            "StrongCoindex (ICN DAA)",
            "Pinta (ICN DAA)"
    };
    public static final Hashtable<String, String> coinsLabelExchangeHashtable = new Hashtable<String, String>()
    {{  put("BTC", "coinbase");
        put("BCH", "coinbase");
        put("ETH", "coinbase");
        put("LTC", "coinbase");

        put("AE", "binance");
        put("ATOM", "kraken");
        put("REP", "bittrex");
        put("BAT", "bittrex");
        put("BNB", "binance");
        put("BCD", "binance");
        put("BSV", "bittrex");
        put("BTG", "bittrex");
        put("BTS", "bittrex");
        put("BLK", "bittrex");
        put("BCN", "hitbtc");
        put("BTM", "cryptocompare");
        put("ADA", "bittrex");
        put("LINK", "binance");
        put("DASH", "bittrex");
        put("DCR", "bittrex");
        put("DGD", "binance");
        put("DOGE", "bittrex");
        put("EOS", "bitfinex");
        put("ETC", "bittrex");
        put("GNO", "bittrex");
        put("GNT", "bittrex");
        put("HSR", "binance");
        put("KIN", "cryptocompare");
        put("ICX", "binance");
        put("ICN", "kraken");
        put("IOST", "kraken");
        put("MIOTA", "binance");
        put("LSK", "bittrex");
        put("LRC", "binance");
        put("MLN", "bittrex");
        put("XMR", "bittrex");
        put("NANO", "binance");
        put("XEM", "bittrex");
        put("NEO", "bittrex");
        put("OMG", "bittrex");
        put("ONT", "binance");
        put("PART", "bittrex");
        put("PPT", "binance");
        put("QTUM", "binance");
        put("RHOC", "cryptocompare");
        put("REQ", "binance");
        put("XRP", "bittrex");
        put("SC", "bittrex");
        put("SNT", "binance");
        put("STEEM", "bittrex");
        put("XLM", "bittrex");
        put("STRAT", "bittrex");
        put("TRTL", "cryptocompare");
        put("TRX", "bitfinex");
        put("VEN", "binance");
        put("XTZ", "bitfinex");
        put("XVG", "bittrex");
        put("VRM", "bittrex");
        put("WTC", "binance");
        put("WAN", "binance");
        put("WAVES", "binance");
        put("ZEC", "bittrex");
        put("ZIL", "binance");
        put("ZRX", "binance");

        put("BLX", "cryptocompare_icndaa");
        put("CCC", "cryptocompare_icndaa");
        put("SOPR", "cryptocompare_icndaa");
        put("FCI", "cryptocompare_icndaa");
        put("GEM", "cryptocompare_icndaa");
        put("PCC", "cryptocompare_icndaa");
        put("WMX", "cryptocompare_icndaa");
        put("BMC", "cryptocompare_icndaa");
        put("AAAX", "cryptocompare_icndaa");
        put("CAR", "cryptocompare_icndaa");
        put("KCOR", "cryptocompare_icndaa");
        put("CBST", "cryptocompare_icndaa");
        put("RCAA", "cryptocompare_icndaa");
        put("BIF", "cryptocompare_icndaa");
        put("CRNC", "cryptocompare_icndaa");
        put("BGA15", "cryptocompare_icndaa");
        put("BEA", "cryptocompare_icndaa");
        put("BLS", "cryptocompare_icndaa");
        put("FCE", "cryptocompare_icndaa");
        put("PPI", "cryptocompare_icndaa");
        put("JJK", "cryptocompare_icndaa");
        put("TRADE", "cryptocompare_icndaa");
        put("EAA", "cryptocompare_icndaa");
        put("SCX", "cryptocompare_icndaa");
        put("CCP", "cryptocompare_icndaa");
    }};
    public static final Hashtable<String, String> coinsLabelGraph = new Hashtable<String, String>()
    {{  put("BTC", "USD");
        put("BCH", "USD");
        put("ETH", "USD");
        put("LTC", "USD");

        put("AE", "BTC");
        put("ATOM", "USD");
        put("REP", "USD");
        put("BAT", "USD");
        put("BNB", "USDT");
        put("BCD", "USD");
        put("BSV", "USD");
        put("BTG", "USDT");
        put("BTS", "USDT");
        put("BLK", "USD");
        put("BCN", "USD");
        put("BTM", "na");
        put("ADA", "USDT");
        put("LINK", "USD");
        put("DASH", "USDT");
        put("DCR", "USD");
        put("DGD", "USD");
        put("DOGE", "USD");
        put("EOS", "USD");
        put("ETC", "USDT");
        put("GNO", "USD");
        put("GNT", "USD");
        put("HSR", "BTC");
        put("KIN", "na");
        put("ICX", "USD");
        put("ICN", "USD");
        put("IOST", "BTC");
        put("MIOTA", "USD");
        put("LSK", "USD");
        put("LRC", "USD");
        put("MLN", "USD");
        put("XMR", "USDT");
        put("NANO", "BTC");
        put("XEM", "USD");
        put("NEO", "USDT");
        put("OMG", "USDT");
        put("ONT", "BTC");
        put("PART", "USD");
        put("PPT", "USD");
        put("QTUM", "USD");
        put("RHOC", "na");
        put("REQ", "USD");
        put("XRP", "USDT");
        put("SC", "USDT");
        put("SNT", "USD");
        put("STEEM", "USD");
        put("XLM", "USD");
        put("STRAT", "USD");
        put("TRX", "USD");
        put("TRTL", "na");
        put("VEN", "USD");
        put("XTZ", "USD");
        put("XVG", "USDT");
        put("VRM", "USD");
        put("WTC", "USD");
        put("WAN", "BTC");
        put("WAVES", "USD");
        put("ZEC", "USDT");
        put("ZIL", "BTC");
        put("ZRX", "USD");

        put("BLX", "na");
        put("CCC", "na");
        put("SOPR", "na");
        put("FCI", "na");
        put("GEM", "na");
        put("PCC", "na");
        put("WMX", "na");
        put("BMC", "na");
        put("AAAX", "na");
        put("CAR", "na");
        put("KCOR", "na");
        put("CBST", "na");
        put("RCAA", "na");
        put("BIF", "na");
        put("CRNC", "na");
        put("BGA15", "na");
        put("BEA", "na");
        put("BLS", "na");
        put("FCE", "na");
        put("PPI", "na");
        put("JJK", "na");
        put("TRADE", "na");
        put("EAA", "na");
        put("SCX", "na");
        put("CCP", "na");
    }};
    public static final Hashtable<String, String> tradingviewCoinSymbol = new Hashtable<String, String>()
    {{  put("BTC", "BTC");
        put("BCH", "BCH");
        put("ETH", "ETH");
        put("LTC", "LTC");

        put("AE", "AE");
        put("ATOM", "ATOM");
        put("REP", "REP");
        put("BAT", "BAT");
        put("BNB", "BNB");
        put("BCD", "BCD");
        put("BSV", "BSV");
        put("BTG", "BTG");
        put("BTS", "BTS");
        put("BLK", "BLK");
        put("BCN", "BCN");
        put("BTM", "BTM");
        put("ADA", "ADA");
        put("LINK", "LINK");
        put("DASH", "DASH");
        put("DCR", "DCR");
        put("DGD", "DGD");
        put("DOGE", "DOGE");
        put("EOS", "EOS");
        put("ETC", "ETC");
        put("GNO", "GNO");
        put("GNT", "GNT");
        put("HSR", "HSR");
        put("KIN", "na");
        put("ICX", "ICX");
        put("ICN", "ICN");
        put("IOST", "IOST");
        put("MIOTA", "IOTA");
        put("LSK", "LSK");
        put("LRC", "LRC");
        put("MLN", "MLN");
        put("XMR", "XMR");
        put("NANO", "NANO");
        put("XEM", "XEM");
        put("NEO", "NEO");
        put("OMG", "OMG");
        put("ONT", "ONT");
        put("PART", "PART");
        put("PPT", "PPT");
        put("QTUM", "QTUM");
        put("RHOC", "na");
        put("REQ", "REQ");
        put("XRP", "XRP");
        put("SC", "SC");
        put("SNT", "SNT");
        put("STEEM", "STEEM");
        put("XLM", "XLM");
        put("STRAT", "STRAT");
        put("TRX", "TRX");
        put("TRTL", "na");
        put("VEN", "VEN");
        put("XTZ", "XTZ");
        put("XVG", "XVG");
        put("VRM", "VRM");
        put("WTC", "WTC");
        put("WAN", "WAN");
        put("WAVES", "WAVES");
        put("ZEC", "ZEC");
        put("ZIL", "ZIL");
        put("ZRX", "ZRX");

        put("BLX", "na");
        put("CCC", "na");
        put("SOPR", "na");
        put("FCI", "na");
        put("GEM", "na");
        put("PCC", "na");
        put("WMX", "na");
        put("BMC", "na");
        put("AAAX", "na");
        put("CAR", "na");
        put("KCOR", "na");
        put("CBST", "na");
        put("RCAA", "na");
        put("BIF", "na");
        put("CRNC", "na");
        put("BGA15", "na");
        put("BEA", "na");
        put("BLS", "na");
        put("FCE", "na");
        put("PPI", "na");
        put("JJK", "na");
        put("TRADE", "na");
        put("EAA", "na");
        put("SCX", "na");
        put("CCP", "na");
    }};
}
