package com.example.cryptodaddies.flexfolio.investment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.CoinlistFragment;
import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinApi;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedRepository;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedStorable;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentRepository;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorableFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Raymond on 2/10/2018.
 */

public class AddInvestmentFragment extends Fragment {
    private View myView;
    private ArrayList<CoinInfo> coinInfoList;
    private HashMap<String, CoinInfo> coinInfoHashMap;
    private String[] COINS;

    private static final InvestmentRepository investmentRepository = new InvestmentRepository();
    private static final FeedRepository feedRepository = new FeedRepository();

    private String[] getCoinsAndPopulateHashMap() {
        ArrayList<String> temp = new ArrayList<>();
        for(CoinInfo c : coinInfoList) {
            String display = c.getSymbol() + " (" + c.getName() + ")";
            temp.add(display);
            coinInfoHashMap.put(display, c);
        }
        String[] sol = new String[temp.size()];
        sol = temp.toArray(sol);
        return sol;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.add_investment_layout, container, false);
        Thread getCoinsThread = coinThread();
        getCoinsThread.run();

        Button submitButton = myView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInvestment(false);
                swapToCoinListView();
            }
        });

        Button flexButton = myView.findViewById(R.id.flexButton);
        flexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInvestment(true);
                swapToCoinListView();
            }
        });

        return myView;
    }

    private void saveInvestment(boolean flex) {
        AutoCompleteTextView coinTextView = myView.findViewById(R.id.edit_coin);
        EditText coinBoughtPrice = myView.findViewById(R.id.edit_coin_bought_price);
        EditText coinBoughtAmount = myView.findViewById(R.id.edit_coin_bought_amount);

        // Grabs amount bought in double
        double coinAmountBoughtDouble= Double.parseDouble(coinBoughtAmount.getText().toString());

        NewProfileActivity mActivity = (NewProfileActivity) getActivity();

        InvestmentStorable investmentStorable = InvestmentStorableFactory.build(
                mActivity.getUserEmail(), coinInfoHashMap.get(coinTextView.getText().toString()).getSymbol(),
                coinBoughtAmount.getText().toString(), coinBoughtPrice.getText().toString());
        investmentRepository.write(investmentStorable);

        if(flex) {
            String status;
            if(coinAmountBoughtDouble == 0) { status = "Now Watching "; }
            else { status = "Invested in "; }
            status = status + coinTextView.getText().toString();
            FeedStorable post = new FeedStorable(mActivity.getUserEmail(), status, System.currentTimeMillis());
            feedRepository.write(post);
        }
    }

    private void swapToCoinListView() {
        NewProfileActivity mActivity = (NewProfileActivity) getActivity();
        FragmentManager fm = mActivity.getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(R.id.content_frame, new CoinlistFragment()).addToBackStack("coinlist").commit();
    }

    private Thread coinThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                setUpCoinResources();
                setUpAutoCompleteTextView();
            }
        });
    }

    public void setUpCoinResources() {
        CoinApi coinApi = ((NewProfileActivity) getActivity()).getCoinApi();
        coinInfoList = coinApi.simpleGetData(false);
        coinInfoHashMap = new HashMap<>();
        COINS = getCoinsAndPopulateHashMap();
    }

    public void setUpAutoCompleteTextView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, COINS);

        AutoCompleteTextView textView = (AutoCompleteTextView)
                myView.findViewById(R.id.edit_coin);
        textView.setAdapter(adapter);
        textView.setValidator(new AutoCompleteValidator());
        textView.setOnFocusChangeListener(new AutoCompleteFocusListener());

    }

    class AutoCompleteValidator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence text) {
            if (coinInfoHashMap.containsKey(text.toString())) {
                TextView currentPrice = myView.findViewById(R.id.coin_current_price);
                currentPrice.setText(Double.toString(coinInfoHashMap.get(text.toString()).getCurrentPrice()));
                EditText boughtPrice = myView.findViewById(R.id.edit_coin_bought_price);
                boughtPrice.setText(Double.toString(coinInfoHashMap.get(text.toString()).getCurrentPrice()));
                return true;
            }

            return false;
        }

        @Override
        public CharSequence fixText(CharSequence invalidText) {
            // This is what the text box is set to after an invalid entry is made
            return "";
        }
    }

    class AutoCompleteFocusListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.edit_coin && !hasFocus) {
                Log.v("Test", "Performing validation");
                ((AutoCompleteTextView)v).performValidation();
            }
        }
    }

}
