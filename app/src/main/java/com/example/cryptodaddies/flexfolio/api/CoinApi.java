package com.example.cryptodaddies.flexfolio.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentRepository;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorableFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CoinApi {

    private Context context;

    public CoinApi(Context context){
        this.context=context;
    }

    // HTTPClient needs to be done asynchrously away from the main thread
    private class DownloadJSONData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget= new HttpGet(urls[0]);

            try {
                HttpResponse response = httpclient.execute(httpget);

                if (response.getStatusLine().getStatusCode() == 200) {
                    String server_response = EntityUtils.toString(response.getEntity());
                    return server_response;
                } else {
                    Log.i("Server response", "Failed to get server response");
                }
            } catch (Exception e) {
                Log.e("Exception: %s", e.toString());
            }

            return "Download failed";
        }
    }

    // Gets coinmarketcap coin data as a jsonobject from coinmarketcap api
    public JsonArray getAllCoinsData(String currencyType) {
        JsonArray data = new JsonArray();
        JsonParser parser = new JsonParser();
        DownloadJSONData task = new DownloadJSONData();
        String response = "";

        Log.i("download", "coindata");
        try {
            response = task.execute(new String[] {
                    "https://api.coinmarketcap.com/v1/ticker/?convert="
                    + currencyType + "&limit=50/" }).get();
        } catch (Exception e) {
            Log.e("Exception: %s", e.toString());
        }

        data = parser.parse(response).getAsJsonArray();

        return data;
    }

    // Get specific coin data
    public JsonObject getCoinData(JsonArray data, String symbol) {
        String sym = "\"" + symbol + "\"";
        for (int i = 0; i < data.size(); i++) {
            JsonObject item = data.get(i).getAsJsonObject();
            String s = item.get("symbol").toString();
            if (s.equals(sym)) {
                return item;
            }
        }

        return null;
    }

    // Get internally stored data
    public JsonArray getData(String filename) {
        FileInputStream inputStream;
        JsonArray data;
        String content = "";

        try {
            inputStream = context.getApplicationContext().openFileInput(filename);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                content = stringBuilder.toString();
            }
        } catch (Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }

        JsonParser parser = new JsonParser();
        data = parser.parse(content).getAsJsonArray();

        return data;
    }

    // Check if there is an active network available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public ArrayList<CoinInvestment> getInvestments(String email, Boolean skipNetworkPull) {
        InvestmentRepository investmentRepository = new InvestmentRepository();
        ArrayList<CoinInvestment> items = investmentRepository.getInvestmentAmounts(email);
        JsonArray data = simpleGetJsonArrayData(skipNetworkPull);

        for (int i = 0; i < items.size(); i++) {
            CoinInvestment item = items.get(i);
            JsonObject itemData = getCoinData(data, item.getSymbol());
            item.setCurrentPrice(Double.parseDouble(getWithoutQuotations(itemData.get("price_usd").toString())));
            item.setValue(item.getAmount() * item.getCurrentPrice());
            item.setPricePercentChange(Double.parseDouble(getWithoutQuotations(itemData.get("percent_change_24h").toString())));
            item.setName(getWithoutQuotations(itemData.get("name").toString()));
            item.setMarketCap(Double.parseDouble(getWithoutQuotations(itemData.get("market_cap_usd").toString())));
            item.setDayVolume(Double.parseDouble(getWithoutQuotations(itemData.get("24h_volume_usd").toString())));
            item.setTotalSupply(Double.parseDouble(getWithoutQuotations(itemData.get("total_supply").toString())));
        }

        return items;
    }

    // Store data in internal storage
    public void saveData(JsonArray data, String filename) {
        String string = data.toString();
        FileOutputStream outputStream;

        try {
            context.getApplicationContext().deleteFile(filename);
            outputStream = context.getApplicationContext()
                    .openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gets Binance graph data
    public JsonArray getBinanceData(String coin, String currency) {
        if (coin.equals("BTC")) { currency = "USDT";}
        if (coin.equals("BCH")) { coin = "BCC";}
        JsonArray data = new JsonArray();
        JsonParser parser = new JsonParser();
        DownloadJSONData task = new DownloadJSONData();
        String response = "";

        try {
            response = task.execute(new String[] {
                    "https://api.binance.com/api/v1/klines?symbol=" + coin + currency + "&interval=1d&limit=31"}).get();
        } catch (Exception e) {
            Log.e("Exception: %s", e.toString());
        }

        try {
            parser.parse(response).getAsJsonArray();
        } catch(Exception e) {
            return new JsonArray();
        }
        data = parser.parse(response).getAsJsonArray();

        return data;
    }

    public JsonArray simpleGetJsonArrayData(Boolean skipNetworkPull) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        long cacheDataTime = sharedPref.getLong("cacheDataTime", 0);
        if ((System.currentTimeMillis() - cacheDataTime) > 10 * 60 * 1000) {
            Log.i("new", Long.toString(System.currentTimeMillis()));
            Log.i("old", Long.toString(cacheDataTime));
            JsonArray ret = getAllCoinsData("USD");
            saveData(ret, "coindata");
            return ret;
        }

        return getData("coindata");
//
//        if (!isNetworkAvailable() || skipNetworkPull) {
//            return getData("coindata");
//        }
//        return getAllCoinsData("USD");
    }

    public ArrayList<CoinInfo> simpleGetData(Boolean skipNetworkPull) {
        // List of Binance coins supported
        List<String> supportedCoins = Arrays.asList("BTC","NCASH","NANO"
                ,"ETH","VEN","NEO","BCPT","LTC","ICX","TRX","DGD","ETC","ADA","BNB","ARN","XRP"
                ,"HSR","EOS","BCH","MTL","CDT","ADX","INS","GVT","IOTA","RPX","RLC","WTC"
                ,"OMG","QTUM","XLM","SNGLS","IOST","ENJ","APPC","XMR","XVG","LSK","BLZ"
                ,"BRD","GAS","EVX","ELF","GTO","BCD","ZRX","POE","GXS","VIBE","AION","DLT"
                ,"STRAT","ZEC","SUB","NEBL","LUN","BTG","VIBE","LEND","MOD","PPT","SALT","LINK"
                ,"CND","TNB","NULS","WINGS","DASH","WAVES","WABI","ENG","EDO","BTS","KNC","REQ"
                ,"POWR","FUN","QSP","MDA","CTR","AST","VIA","OST","LRC","RDN","AMB","BQX","ARK"
                ,"PIVX","XZC","FUEL","SNM","AE","MCO","YOYO","SNT","TRIG","CHAT","DNT","MANA","RCN"
                ,"OAX","STEEM","CMT","BAT","STORJ","TNT","KMD","MTH","NAV","ICN","BNT");
        JsonArray data = simpleGetJsonArrayData(skipNetworkPull);
        ArrayList<CoinInfo> items = new ArrayList<CoinInfo>();

        // remember to add code to account for currency conversion
        for (int i = 0; i < data.size(); i++) {
            JsonObject item = data.get(i).getAsJsonObject();
            if (supportedCoins.contains(getWithoutQuotations(item.get("symbol").toString()))) {
                items.add(new CoinInfo(Double.parseDouble(getWithoutQuotations(item.get("price_btc").toString())),
                        Double.parseDouble(getWithoutQuotations(item.get("price_usd").toString())),
                        Double.parseDouble(getWithoutQuotations(item.get("percent_change_24h").toString())),
                        getWithoutQuotations(item.get("name").toString()),
                        getWithoutQuotations(item.get("symbol").toString()),
                        Double.parseDouble(getWithoutQuotations(item.get("24h_volume_usd").toString())),
                        Double.parseDouble(getWithoutQuotations(item.get("market_cap_usd").toString())),
                        Double.parseDouble(getWithoutQuotations(item.get("total_supply").toString()))));

            }
        }
        return items;
    }

    public Map<String, Double> getCurrentCoinValueMap(Boolean skipNetworkPull) {
        JsonArray data = simpleGetJsonArrayData(skipNetworkPull);
        HashMap<String, Double> map = new HashMap<>();

        // remember to add code to account for currency conversion
        for (int i = 0; i < data.size(); i++) {
            JsonObject item = data.get(i).getAsJsonObject();
            map.put(getWithoutQuotations(item.get("symbol").toString()),
                    Double.parseDouble(getWithoutQuotations(item.get("price_usd").toString())));
        }
        return map;
    }

    private String getWithoutQuotations(String s) {
        return s.substring(1, s.length()-1);
    }
}
