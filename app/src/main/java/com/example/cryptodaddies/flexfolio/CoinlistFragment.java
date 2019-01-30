package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.investment.AddInvestmentFragment;

public class CoinlistFragment extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.coinlist_layout, container, false);
        setUpFloatingActionButton();

        return myView;
    }

    private void setUpFloatingActionButton() {
        FloatingActionButton fab = myView.findViewById(R.id.clfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabOnClick();
            }
        });
    }

    private void fabOnClick() {
        System.out.println("Fab pressed");
        FragmentManager fm = getActivity().getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(R.id.content_frame, new AddInvestmentFragment())
                .addToBackStack("coinlist").commit();
    }
}
