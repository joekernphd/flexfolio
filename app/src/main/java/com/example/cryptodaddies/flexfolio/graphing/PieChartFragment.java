package com.example.cryptodaddies.flexfolio.graphing;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.cryptodaddies.flexfolio.aesthetics.Palette;

/**
 * Created by Raymond on 1/31/2018.
 */

public class  PieChartFragment extends Fragment {
    private static String TAG = "PieChartFragment";
    private static ArrayList<Float> DEFAULT_Y_DATA = new ArrayList<Float>(Arrays.asList(25.3f, 10.6f, 66.76f, 46.01f,
            16.89f, 23.9f));
    private static ArrayList<String> DEFAULT_X_DATA = new ArrayList<String>(Arrays.asList("Raymond", "Ronson", "Robinson",
            "Joe", "Xhoni", "John"));
    private static ArrayList<Integer> DEFAULT_COLORS = new ArrayList<Integer>(Arrays.asList(Color.GRAY, Color.BLUE, Color.RED,
            Color.GREEN, Color.YELLOW, Color.CYAN, Color.YELLOW, Color.MAGENTA));
    private static ArrayList<Integer> DEFAULT_COLORS_2 = new ArrayList<Integer>(Arrays.asList(Palette.hbBlue,
            Palette.tpBlue, Palette.hbRed, Palette.hbGrey, Palette.tpSalmon, Palette.tpSand, Palette.tpSnow, Palette.tpPink));
    private static Description DEFAULT_DESCRIPTION = getDefaultDescription();
    private static final String DEFAULT_TITLE = "";

    private static Description getDefaultDescription() {
        Description description = new Description();
        description.setText("");
        description.setTextSize(18);
        return description;
    }


    protected PieChartFactory pieChartFactory = new PieChartFactory();
    protected PieChart pieChart;
    protected View myView;
    protected String email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.piechart_layout, container, false);
        Log.d(TAG, "OnCreate - making pie chart");

        email = ((NewProfileActivity)getActivity()).getUserEmail();

        setupPieChart(email);

        return myView;
    }

    protected void setupPieChart(String email) {
        PieChartData investments =
                ((NewProfileActivity)getActivity()).getInvestments(email);
        investments.convertDataCurrencyValues(((NewProfileActivity) getActivity()).getUserCurrency());
        ArrayList<Float> yData = investments.getyData();
        ArrayList<String> xData = investments.getxData();

        if(yData.isEmpty()) {
            pieChart = pieChartFactory.getChart(myView.findViewById(R.id.pieChart), DEFAULT_TITLE, DEFAULT_DESCRIPTION,
                    DEFAULT_COLORS_2, DEFAULT_Y_DATA, DEFAULT_X_DATA);
        } else {
            pieChart = pieChartFactory.getChart(myView.findViewById(R.id.pieChart), DEFAULT_TITLE, DEFAULT_DESCRIPTION,
                    DEFAULT_COLORS_2, yData, xData);
        }
    }

    public void refreshPieChart() {
        System.out.println(email);
        PieChartData investments =
                ((NewProfileActivity)getActivity()).getInvestments(email);
        investments.convertDataCurrencyValues(((NewProfileActivity) getActivity()).getUserCurrency());
        ArrayList<Float> yData = investments.getyData();
        ArrayList<String> xData = investments.getxData();


        System.out.println(yData.toString());

        pieChart.clear();

        if(yData.isEmpty()) {
            pieChart = pieChartFactory.getChart(myView.findViewById(R.id.pieChart), DEFAULT_TITLE, DEFAULT_DESCRIPTION,
                    DEFAULT_COLORS_2, DEFAULT_Y_DATA, DEFAULT_X_DATA);
        } else {
            pieChart = pieChartFactory.getChart(myView.findViewById(R.id.pieChart), DEFAULT_TITLE, DEFAULT_DESCRIPTION,
                    DEFAULT_COLORS_2, yData, xData);
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
