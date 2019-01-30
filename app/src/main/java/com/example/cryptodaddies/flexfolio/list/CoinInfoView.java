package com.example.cryptodaddies.flexfolio.list;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;

import currency.currencyTable;

public class CoinInfoView extends RelativeLayout {

    private TextView currentPrice;
    private TextView name;
    private TextView pricePercentChange;
    private TextView symbol;

    public static CoinInfoView inflate(ViewGroup parent) {
        CoinInfoView coinInfoView = (CoinInfoView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin_info, parent, false);
        return coinInfoView;
    }

    public CoinInfoView(Context c) {
        this(c, null);
    }

    public CoinInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoinInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.item_view_coininfo, this, true);
        setupChildren();
    }

    private void setupChildren() {
        currentPrice = (TextView) findViewById(R.id.item_currentPrice);
        name = (TextView) findViewById(R.id.item_name);
        pricePercentChange = (TextView) findViewById(R.id.item_pricePercentChange);
        symbol = (TextView) findViewById(R.id.item_symbol);
    }

    public void setItem(CoinInfo item) {
        NewProfileActivity activity = (NewProfileActivity) getContext();
        String userCurrency = activity.getUserCurrency();
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
        currentPrice.setText(currencySymbol + String.format("%.4f", item.getCurrentPrice() * currencyTable.getUsdConversion(userCurrency)));
        name.setText(item.getName());
        if (item.getPricePercentChange() < 0) {
            pricePercentChange.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red,
                    null));
            pricePercentChange.setText(item.getPricePercentChange().toString() + "%");
        } else {
            pricePercentChange.setTextColor(ResourcesCompat.getColor(getResources(), R.color.green,
                    null));
            pricePercentChange.setText("+" + item.getPricePercentChange().toString() + "%");
        }
        symbol.setText(item.getSymbol());
    }


    public TextView getCurrentPrice() {
        return currentPrice;
    }

    public TextView getName() {
        return name;
    }

    public TextView getPricePercentChange() {
        return pricePercentChange;
    }
}