package com.example.cryptodaddies.flexfolio.graphing;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Raymond on 1/31/2018.
 */

public class PieChartFactory {

    private static String TAG = "PieChartFactory";
    private PieDataSetFactory pieDataSetFactory = new PieDataSetFactory();

    public PieChartFactory() {

    }

    public PieChart getChart(View chartView, String label, Description description, ArrayList<Integer> colors,
                             ArrayList<Float> yData, ArrayList<String> xData) {
        PieChart pieChart = (PieChart) chartView;
        pieChart = setVisualAttributes(pieChart, label, description);

        PieData pieData = new PieData(pieDataSetFactory.getPieDataSet(label, yData, xData, colors));
        pieChart.getLegend().setEnabled(false);
        //Legend legend = buildLegend(pieChart.getLegend());

        pieChart.setData(pieData);
        pieChart.setExtraOffsets(30f, 30f, 30f, 30f);
        pieChart.invalidate();

        return pieChart;
    }

    private Legend buildLegend(Legend legend) {
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setTextSize(18);
        return legend;
    }

    private PieChart setVisualAttributes(PieChart pieChart, String label, Description description) {
        pieChart.setDescription(description);
        pieChart.setDrawHoleEnabled(false);
        return pieChart;
    }

    public PieDataSetFactory getPieDataSetFactory() {
        return pieDataSetFactory;
    }

    public void setPieDataSetFactory(PieDataSetFactory pieDataSetFactory) {
        this.pieDataSetFactory = pieDataSetFactory;
    }
}
