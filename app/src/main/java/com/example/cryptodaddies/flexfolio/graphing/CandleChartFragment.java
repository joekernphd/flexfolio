package com.example.cryptodaddies.flexfolio.graphing;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.CoinInfoFragment;
import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.ChartInfo;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ronson on 2/3/18.
 */

public class CandleChartFragment extends Fragment {

    View myView;
    String high;
    String low;
    CandleStickChart candleChart;
    CandleChartFactory candleChartFactory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        CoinInfoFragment parent = (CoinInfoFragment) getParentFragment();
        NewProfileActivity activity = (NewProfileActivity) getActivity();

        myView = inflater.inflate(R.layout.candlechart_layout, container, false);

        // grab coin symbol from parent fragment
        String symbol = getCoinSymbol(parent);
        Log.d("TAG", symbol);


        // use symbol to pull binance candlestick data (remember to factor in currency conversion)
        JsonArray data = activity.passBinanceData(symbol, "BTC");
        ArrayList<ChartInfo> items = new ArrayList<ChartInfo>();

        for (int i = 0; i < data.size(); i++) {
            JsonArray item = data.get(i).getAsJsonArray();

            items.add(new ChartInfo(item.get(0).getAsLong(), item.get(6).getAsLong(),
                    Float.parseFloat(getWithoutQuotations(item.get(1).toString())),
                    Float.parseFloat(getWithoutQuotations(item.get(4).toString())),
                    Float.parseFloat(getWithoutQuotations(item.get(2).toString())),
                    Float.parseFloat(getWithoutQuotations(item.get(3).toString()))));

            if (i == data.size() - 1) {
                NumberFormat formatter = new DecimalFormat("#.00");

                high = String.format("%.2f", Double.parseDouble(getWithoutQuotations(item.get(2).toString())));
                low = String.format("%.2f", Double.parseDouble(getWithoutQuotations(item.get(3).toString())));
            }

        }
        // create candlestick chart


        // make changes to factory to pass in binance data (or object with binance data)
        candleChartFactory = new CandleChartFactory(items);
        candleChart = candleChartFactory.getChart(myView.findViewById(R.id.candleChart));


        return myView;
    }

    public String getCoinSymbol(CoinInfoFragment parent){
        String symbol = parent.getSymbol();
        return symbol;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getWithoutQuotations(String s) {
        return s.substring(1, s.length()-1);
    }

    // METHODS TO UPDATE VALUES IN PARENT FRAGMENT

    // setHighValue

    // setLowValue

    // setBid

    // setAsk

}
