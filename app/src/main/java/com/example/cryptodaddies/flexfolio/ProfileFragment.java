package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.graphing.PieChartFragment;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

import currency.currencyTable;

public class ProfileFragment extends Fragment {
    View myView;
    private String userCurrency;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.profile_layout, container, false);

        String userEmail = ((NewProfileActivity) getActivity()).getUserEmail();
        UserRepository userRepository = new UserRepository();
        UserStorable userStorable = userRepository.read(userEmail);
        userCurrency = userStorable.getCurrency();

        return myView;
    }

    public void refresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBalance();
        updateCurrency();
        setup();

        FragmentManager fm = getFragmentManager();
        PieChartFragment pieChartFragment = (PieChartFragment) fm.findFragmentById(R.id.pie_chart_frame);
        if(pieChartFragment != null) {
            pieChartFragment.refreshPieChart();
//            Toast.makeText(getContext(), "refresh pie chart", Toast.LENGTH_SHORT).show();
        }
        else {
//            Toast.makeText(getContext(), "refresh pie chart FAIL", Toast.LENGTH_SHORT).show();
        }
    }

    private void setup() {
        ScrollView scrollView = (ScrollView) myView.findViewById(R.id.scroll);
        View child = myView.findViewById(R.id.balance_header);
//        scrollView.requestChildFocus(child, child);

        View v = myView.findViewById(R.id.investmentListFragment);
        NewProfileActivity activity = (NewProfileActivity) getActivity();
        v.setMinimumHeight(275 * activity.passCoinInvestmentArrayList(false).size());

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.pie_chart_frame, new PieChartFragment()).commit();
    }

    private void updateBalance() {
        NewProfileActivity mActivity = (NewProfileActivity) getActivity();
        Float balance = mActivity.getUserBalance();
        double cashInvested = mActivity.getCashInvested();

        double changeNumber = balance - cashInvested;
        double changePercent = balance/cashInvested;

        //percent decrease or increase
        if(changeNumber < 0) {
            changePercent = 1 - changePercent;
        } else {
            changePercent = changePercent - 1;
        }
        changePercent = changePercent * 100;

        TextView balanceView = myView.findViewById(R.id.balance_number);
        TextView changeNumberView = myView.findViewById(R.id.change_number);
        TextView changePercentView = myView.findViewById(R.id.change_percent);
        TextView changeSymbolView = myView.findViewById(R.id.change_symbol);
        balanceView.setText(String.format("%.2f", balance));

        switch (userCurrency) {
            case "USD":
                changeSymbolView.setText("$");
                break;
            case "EUR":
                changeSymbolView.setText("€");
                break;
            case "GBP":
                changeSymbolView.setText("£");
                break;
            case "BTC":
                changeSymbolView.setText("B");
                break;
            case "ETH":
                changeSymbolView.setText("E");
                break;
        }

        if(changeNumber < 0) {
            changeNumberView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red,
                    null));
            changePercentView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red,
                    null));
            changeSymbolView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.red,
                    null));
            changeNumber = changeNumber * -1;
            changeSymbolView.setText("-" + changeSymbolView.getText());
            changePercentView.setText("(-" + String.format("%.2f", changePercent) + "%)");
        }
        else {
            changeSymbolView.setText("+" + changeSymbolView.getText());
            changePercentView.setText("(+" + String.format("%.2f", changePercent) + "%)");
        }
        changeNumberView.setText(String.format("%.2f", changeNumber));

    }

    private void updateCurrency() {
        TextView amountDisplay = myView.findViewById(R.id.balance_number);
        TextView symbol = myView.findViewById(R.id.currencySymbol);
        TextView changeNumberView = myView.findViewById(R.id.change_number);
        String temp = amountDisplay.getText().toString();
        double amount = Double.parseDouble(temp);
        temp = changeNumberView.getText().toString();
        double changeNumber = Double.parseDouble(temp);
        switch(userCurrency){
            case "USD":
                symbol.setText("$");
                break;
            case "EUR":
                symbol.setText("€");
                break;
            case "GBP":
                symbol.setText("£");
                break;
            case "BTC":
                symbol.setText("B");
                break;
            case "ETH":
                symbol.setText("E ");
                break;
        }
        amount = amount * currencyTable.getUsdConversion(userCurrency);
        changeNumber = changeNumber * currencyTable.getUsdConversion(userCurrency);
        amountDisplay.setText(String.format("%1$,.2f", amount));
        changeNumberView.setText(String.format("%1$,.2f", changeNumber));
    }
}
