package com.example.cryptodaddies.flexfolio.api;

public class CoinInvestment {
    private Double amount;
    private Double currentPrice;
    private Double portfolioPercentage;
    private Double pricePercentChange;
    private Double value;
    private String name;
    private String symbol;

    // for graph/info fragment
    private Double dayVolume;
    private Double marketCap;
    private Double totalSupply;

    public CoinInvestment(Double amount, Double pricePercentChange, Double value,
                          Double currentPrice, String name) {
        super();
        this.amount = amount;
        this.currentPrice = currentPrice;
        this.pricePercentChange = pricePercentChange;
        this.value = value;
        this.name = name;
    }

    public CoinInvestment(String symbol, Double amount) {
        super();
        this.amount = amount;
        this.symbol = symbol;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Double getPortfolioPercentage() {
        return portfolioPercentage;
    }

    public Double getPricePercentChange() {
        return pricePercentChange;
    }

    public Double getValue() {
        return value;
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

    public Double getTotalSupply() {
        return totalSupply;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPortfolioPercentage(Double portfolioPercentage) {
        this.portfolioPercentage = portfolioPercentage;
    }

    public void setPricePercentChange(Double pricePercentChange) {
        this.pricePercentChange = pricePercentChange;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setDayVolume(Double dayVolume) {
        this.dayVolume = dayVolume;
    }

    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    public void setTotalSupply(Double totalSupply) {
        this.totalSupply = totalSupply;
    }
}
