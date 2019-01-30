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
import com.example.cryptodaddies.flexfolio.api.CoinInvestment;

import currency.currencyTable;

public class CoinInvestmentView extends RelativeLayout {
    private TextView amount;
    private TextView currentPrice;
    private TextView name;
    private TextView portfolioPercentage;
    private TextView pricePercentChange;
    private TextView value;

    public static CoinInvestmentView inflate(ViewGroup parent) {
        CoinInvestmentView coinInvestmentView = (CoinInvestmentView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin_investment, parent, false);
        return coinInvestmentView;
    }

    public CoinInvestmentView(Context c) {
        this(c, null);
    }

    public CoinInvestmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoinInvestmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.item_view_coininvestment, this, true);
        setupChildren();
    }

    private void setupChildren() {
        amount = (TextView) findViewById(R.id.item_amount);
        currentPrice = (TextView) findViewById(R.id.item_currentPrice);
        name = (TextView) findViewById(R.id.item_name);
//        portfolioPercentage = (TextView) findViewById(R.id.item_portfolioPercentage);
        pricePercentChange = (TextView) findViewById(R.id.item_pricePercentChange);
        value = (TextView) findViewById(R.id.item_value);
    }

    public void setItem(CoinInvestment item) {
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
        amount.setText(item.getAmount().toString());
//        portfolioPercentage.setText("(" + item.getPortfolioPercentage().toString() + "%)");
        value.setText("(" + currencySymbol + item.getValue().toString() + ")");
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
    }

    public TextView getAmount() {
        return amount;
    }

    public TextView getCurrentPrice() {
        return currentPrice;
    }

    public TextView getName() {
        return name;
    }

    public TextView getPortfolioPercentage() {
        return portfolioPercentage;
    }

    public TextView getPricePercentChange() {
        return pricePercentChange;
    }

    public TextView getValue() {
        return value;
    }
}