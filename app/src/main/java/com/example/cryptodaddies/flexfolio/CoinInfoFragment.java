package com.example.cryptodaddies.flexfolio;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.graphing.CandleChartFragment;

import org.w3c.dom.Text;

import currency.currencyTable;

/**
 * Created by ronson on 2/3/18.
 */

public class CoinInfoFragment extends Fragment{

    private Double currentPrice;
    private Double pricePercentChange;
    private String name;
    private String symbol;
    private Double dayVolume;
    private Double marketCap;
    private Double totalSupply;

    // textviews
    private TextView currentPriceView;
    private TextView nameView;
    private TextView pricePercentChangeView;
    private TextView dayVolumeView;
    private TextView marketCapView;
    private TextView totalSupplyView;
    private TextView highView;
    private TextView lowView;
    public CoinInfoFragment() {}


    View myView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.coininfo_layout, container, false);
        NewProfileActivity a = (NewProfileActivity) getActivity();
        String userCurrency = a.getUserCurrency();
        String currencySymbol = "$";
        switch (userCurrency) {
            case "USD":
                currencySymbol = "$";
                break;
            case "ETH":
                currencySymbol = "E";
                break;
            case "BTC":
                currencySymbol = "B";
                break;
            case "EUR":
                currencySymbol = "€";
                break;
            case "GBP":
                currencySymbol = "£";
                break;
        }
        nameView = (TextView) myView.findViewById(R.id.current_price);
        nameView.setText(name + " price: ");

        // remember to change symbol
        currentPriceView = (TextView) myView.findViewById(R.id.balance_number);
        currentPriceView.setText(currencySymbol + String.format("%.4f", (currentPrice * currencyTable.getUsdConversion(userCurrency))));

        pricePercentChangeView = (TextView) myView.findViewById(R.id.changeValue);
        pricePercentChangeView.setText(Double.toString(pricePercentChange) + "%");

        dayVolumeView = (TextView) myView.findViewById(R.id.volumeValue);
        dayVolumeView.setText(currencySymbol + withSuffix(dayVolume * currencyTable.getUsdConversion(userCurrency)));

        marketCapView = (TextView) myView.findViewById(R.id.marketCapValue);
        marketCapView.setText(currencySymbol + withSuffix(marketCap * currencyTable.getUsdConversion(userCurrency)));

        totalSupplyView = (TextView) myView.findViewById(R.id.supplyValue);
        totalSupplyView.setText(withSuffix(totalSupply));

        highView = (TextView) myView.findViewById(R.id.highValue);
        highView.setText(currencySymbol);

        lowView = (TextView) myView.findViewById(R.id.lowValue);
        lowView.setText(currencySymbol);

//        FragmentManager fm = getFragmentManager();
//        Fragment candleChartFragment = getFragmentManager().findFragmentById(R.id.fragmentCandleChart);
//        Bundle args = new Bundle();
//        args.putString("A", "TEST");
//
//        candleChartFragment.setArguments(args);

        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        CandleChartFragment ccf = (CandleChartFragment)getChildFragmentManager().findFragmentById(R.id.fragmentCandleChart);

        NewProfileActivity a = (NewProfileActivity) getActivity();

        // GET BACK TO THIS
        String userCurrency = a.getUserCurrency();
        Double convertHigh = 0.0;
        Double convertLow = 0.0;
        // alternative: change cmc api to store btc value in all coins, then use currencytable to convert

        // if BTC symbol, convert usd -> other
        if (symbol.equals("BTC")) {
            // use function for conversion
            convertHigh = Double.parseDouble(ccf.getHigh()) * currencyTable.getUsdConversion(userCurrency);
            convertLow = Double.parseDouble(ccf.getLow()) * currencyTable.getUsdConversion(userCurrency);
        }
        else {
            // btc -> usd -> other
            convertHigh = (Double.parseDouble(ccf.getHigh())/currencyTable.getUsdConversion("BTC")) * currencyTable.getUsdConversion(userCurrency);
            convertLow = (Double.parseDouble(ccf.getLow())/currencyTable.getUsdConversion("BTC")) * currencyTable.getUsdConversion(userCurrency);
        }

        // set high/low
        highView = myView.findViewById(R.id.highValue);
        highView.setText(highView.getText().toString() + String.format("%.2f", convertHigh));

        lowView = myView.findViewById(R.id.lowValue);
        lowView.setText(lowView.getText().toString() + String.format("%.2f", convertLow));
    }


    public void setData(String name, String symbol, Double currentPrice, Double pricePercentChange, Double dayVolume, Double marketCap, Double totalSupply) {
        this.name = name;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.pricePercentChange = pricePercentChange;
        this.dayVolume = dayVolume;
        this.marketCap = marketCap;
        this.totalSupply = totalSupply;
    }

    public String getSymbol() {return symbol;}

    public static String withSuffix(Double count) {
        if (count < 1000) {
            return "" + count;
        }
        int exp = (int)(Math.log(count)/Math.log(1000));
        return String.format("%.3f %c", count / Math.pow(1000,exp), "kMBT".charAt(exp-1));
    }





}
