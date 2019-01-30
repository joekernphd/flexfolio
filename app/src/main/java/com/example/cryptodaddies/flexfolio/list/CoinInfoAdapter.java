package com.example.cryptodaddies.flexfolio.list;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.CoinInfoFragment;
import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;

import java.util.List;

public class CoinInfoAdapter extends ArrayAdapter<CoinInfo> {

    public CoinInfoAdapter(Context c, List<CoinInfo> info) {
        super (c, 0, info);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CoinInfoView coinInfoView = (CoinInfoView) convertView;
        if (null == coinInfoView)
            coinInfoView = CoinInfoView.inflate(parent);
        coinInfoView.setItem(getItem(position));
        TextView nameTextView = coinInfoView.getName();
        String coin = nameTextView.getText().toString();
        coinInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // for debugging only
                String msg = "POS NAME: " + coin;
                Log.d("@@@TESTCLICK", msg);
                Activity activity = (NewProfileActivity)parent.getContext();
                CoinInfo item = getItem(position);

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

        return coinInfoView;
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
