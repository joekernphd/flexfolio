package com.example.cryptodaddies.flexfolio.api;


import android.util.Log;

import currency.currencyTable;

public class CoinInfo {
    private Double currentPrice; // in USD
    private Double priceAsBTC; // in BTC
    private Double pricePercentChange;
    private String name;
    private String symbol;

    // for graph/info fragment
    private Double dayVolume;
    private Double marketCap;
    private Double totalSupply;

    public CoinInfo(Double priceAsBTC, Double currentPrice, Double pricePercentChange, String name, String symbol,
                    Double dayVolume, Double marketCap, Double totalSupply) {
        super();
        this.priceAsBTC = priceAsBTC;
        this.currentPrice = currentPrice;
        this.pricePercentChange = pricePercentChange;
        this.name = name;
        this.symbol = symbol;
        this.dayVolume = dayVolume;
        this.marketCap = marketCap;
        this.totalSupply = totalSupply;

        if (symbol.equals("BTC")) {
            currencyTable.updateBTC(1/currentPrice);
        }

        if (symbol.equals("ETH")) {
            currencyTable.updateETH(1/currentPrice);
        }
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Double getPricePercentChange() {
        return pricePercentChange;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getDayVolume() {
        return dayVolume;
    }

    public Double getMarketCap() {
        return marketCap;
    }

    public Double getTotalSupply() {return totalSupply; }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPricePercentChange(Double pricePercentChange) {
        this.pricePercentChange = pricePercentChange;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
