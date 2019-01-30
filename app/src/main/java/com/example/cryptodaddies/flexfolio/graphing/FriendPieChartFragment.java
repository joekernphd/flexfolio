package com.example.cryptodaddies.flexfolio.graphing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;

import java.util.ArrayList;

/**
 * Created by Raymond on 3/3/2018.
 */

public class FriendPieChartFragment extends PieChartFragment {
    private String TAG = "FriendPieChartFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.piechart_layout, container, false);
        Log.d(TAG, "OnCreate - making FRIEND pie chart");

        email = ((NewProfileActivity)getActivity()).getFriendEmail();
        pieChartFactory.getPieDataSetFactory().setDrawValues(false);

        setupPieChart(email);
        return myView;
    }
}
