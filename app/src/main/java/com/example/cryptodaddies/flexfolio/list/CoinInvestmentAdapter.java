package com.example.cryptodaddies.flexfolio.list;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.CoinInfoFragment;
import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.ProfileFragment;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinInvestment;

import java.util.List;

public class CoinInvestmentAdapter extends ArrayAdapter<CoinInvestment> {

    CoinInvestmentView coinInvestmentView;

    public CoinInvestmentAdapter(Context c, List<CoinInvestment> info) {
        super (c, 0, info);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        coinInvestmentView = (CoinInvestmentView) convertView;
        if (null == coinInvestmentView)
            coinInvestmentView = CoinInvestmentView.inflate(parent);
        coinInvestmentView.setItem(getItem(position));
        TextView nameTextView = coinInvestmentView.getName();
        String coin = nameTextView.getText().toString();
        coinInvestmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // for debugging only
                String msg = "POS NAME: " + coin;
                Log.d("@@@TESTCLICK", msg);
                Activity activity = (NewProfileActivity)parent.getContext();
                CoinInvestment item = getItem(position);

                // Set up data to feed to CoinInfoFragment
                String name = item.getName();
                String symbol = item.getSymbol();
                Double currentPrice = item.getCurrentPrice();
                Double pricePercentChange = item.getPricePercentChange();
                Double dayVolume = item.getDayVolume();
                Double marketCap = item.getMarketCap();
                Double totalSupply = item.getTotalSupply();
                changeFragmentFromAdapter(activity, R.id.content_frame, name, symbol, currentPrice, pricePercentChange, dayVolume, marketCap, totalSupply);
            }
        });

        coinInvestmentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                NewProfileActivity activity = (NewProfileActivity) parent.getContext();
                CoinInvestment item = getItem(position);
//                Toast.makeText(getContext(), "long click " + item.getSymbol(), Toast.LENGTH_SHORT).show();

                FragmentManager fragmentManager = activity.getFragmentManager();
                activity.showContextMenu(item.getSymbol());
                return true;
            }
        });

        return coinInvestmentView;
    }

    // switch to coinInfoFragment
    public void changeFragmentFromAdapter(Activity act, int layoutid, String name, String symbol, Double currentPrice,
                                          Double pricePercentChange, Double dayVolume, Double marketCap, Double totalSupply) {

        CoinInfoFragment coinInfoFragment = new CoinInfoFragment();

        coinInfoFragment.setData(name, symbol, currentPrice, pricePercentChange, dayVolume, marketCap, totalSupply);

        FragmentManager fragmentManager = act.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(layoutid, coinInfoFragment).addToBackStack("coinlist").commit();
    }
}
