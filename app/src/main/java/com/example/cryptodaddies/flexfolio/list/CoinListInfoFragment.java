package com.example.cryptodaddies.flexfolio.list;

import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;

import java.util.ArrayList;

public class CoinListInfoFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment, container, false); //super.onCreateView(inflater, container, savedInstanceState);
        Thread listFillerThread = listFillerThread();
        listFillerThread.run();

        return v;
    }

    public Thread listFillerThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                NewProfileActivity activity = (NewProfileActivity) getActivity();
                ArrayList<CoinInfo> items = activity.passCoinInfoArrayList(false);
                setListAdapter(new CoinInfoAdapter(activity, items));
            }
        });
    }
}
