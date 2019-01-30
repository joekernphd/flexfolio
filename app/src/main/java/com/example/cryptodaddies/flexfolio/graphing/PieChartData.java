package com.example.cryptodaddies.flexfolio.graphing;

import java.util.ArrayList;

import currency.currencyTable;

/**
 * Created by Raymond on 2/11/2018.
 */

public class PieChartData {
    private ArrayList<Float> yData = new ArrayList<>();
    private ArrayList<String> xData = new ArrayList<>();

    public PieChartData(ArrayList<Float> yData, ArrayList<String> xData) {
        this.yData = yData;
        this.xData = xData;
    }

    public ArrayList<Float> getyData() {
        return yData;
    }

    public void setyData(ArrayList<Float> yData) {
        this.yData = yData;
    }

    public ArrayList<String> getxData() {
        return xData;
    }

    public void setxData(ArrayList<String> xData) {
        this.xData = xData;
    }

    public void convertDataCurrencyValues(String currency) {
        for(int i = 0; i < yData.size(); i++) {
            yData.set(i,yData.get(i) * currencyTable.getUsdConversion(currency).floatValue());
        }
    }

    @Override
    public String toString() {
        return "PieChartData{" +
                "yData=" + yData +
                ", xData=" + xData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PieChartData that = (PieChartData) o;

        if (yData != null ? !yData.equals(that.yData) : that.yData != null) return false;
        return xData != null ? xData.equals(that.xData) : that.xData == null;
    }

    @Override
    public int hashCode() {
        int result = yData != null ? yData.hashCode() : 0;
        result = 31 * result + (xData != null ? xData.hashCode() : 0);
        return result;
    }
}
