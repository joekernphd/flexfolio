package com.example.cryptodaddies.flexfolio.graphing;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * Created by Raymond on 1/31/2018.
 */

public class PieDataSetFactory {
    private static String TAG = "PieDataSetFactory";
    private static Integer DEFAULT_TEXT_COLOR = Color.parseColor("#000000");
    private static Integer DEFAULT_TEXT_SIZE = 16;
    private Boolean drawValues = true;
    private Boolean showWatching = false;

    public PieDataSet getPieDataSet(String label, ArrayList<Float> yData, ArrayList<String> xData, ArrayList<Integer> colors) {
        Log.d(TAG, "Adding data to pieChart");
        PieDataSet dataSet = new PieDataSet(convertToPieEntries(yData, xData), label);
        dataSet.setColors(colors);
        dataSet.setValueTextSize(DEFAULT_TEXT_SIZE);
        dataSet.setValueTextColor(DEFAULT_TEXT_COLOR);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(10f);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.45f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setDrawValues(drawValues);
        //.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        return dataSet;
    }

    private ArrayList<PieEntry> convertToPieEntries(ArrayList<Float> yData, ArrayList<String> xData) {
        ArrayList<PieEntry> newList = new ArrayList<PieEntry>();
        for(int i = 0; i < yData.size(); i++) {
            if(showWatching || Math.abs(yData.get(i)) > 0.0001) {
                System.out.println("Data is: " + yData.get(i));
                newList.add(new PieEntry(yData.get(i), xData.get(i)));
            }
        }

        return newList;
    }

    public Boolean getDrawValues() {
        return drawValues;
    }

    public void setDrawValues(Boolean drawValues) {
        this.drawValues = drawValues;
    }
}
