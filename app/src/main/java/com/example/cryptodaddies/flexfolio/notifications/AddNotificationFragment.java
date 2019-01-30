package com.example.cryptodaddies.flexfolio.notifications;

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
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.CoinlistFragment;
import com.example.cryptodaddies.flexfolio.NewProfileActivity;
import com.example.cryptodaddies.flexfolio.ProfileFragment;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinApi;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedRepository;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedStorable;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentRepository;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorableFactory;
import com.example.cryptodaddies.flexfolio.persistence.notifications.NotificationRepository;
import com.example.cryptodaddies.flexfolio.persistence.notifications.NotificationStorable;
import com.example.cryptodaddies.flexfolio.persistence.notifications.NotificationStorableFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Raymond on 2/10/2018.
 */

public class AddNotificationFragment extends Fragment {
    private View myView;
    private ArrayList<CoinInfo> coinInfoList;
    private HashMap<String, CoinInfo> coinInfoHashMap;
    private String[] COINS;

    private static final NotificationRepository notificationRepository = new NotificationRepository();

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
        myView = inflater.inflate(R.layout.add_notification_layout, container, false);
        Thread getCoinsThread = coinThread();
        getCoinsThread.run();

        Button submitButton = myView.findViewById(R.id.submitNotificationButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotification();
                swapProfileView();
            }
        });

        return myView;
    }

    private void saveNotification() {
        AutoCompleteTextView coinTextView = myView.findViewById(R.id.notification_edit_coin);
        EditText thresholdView = myView.findViewById(R.id.threshold_price);
        RadioButton aboveButton = myView.findViewById(R.id.radioButtonAbove);


        // Grabs amount bought in double
        double thresholdPrice = Double.parseDouble(thresholdView.getText().toString());

        int aboveOrBelow = 0;
        if(aboveButton.isChecked()) {
            aboveOrBelow = 1;
        }



        NewProfileActivity mActivity = (NewProfileActivity) getActivity();

        NotificationStorable notificationStorable = NotificationStorableFactory.build(
                mActivity.getUserEmail(), coinInfoHashMap.get(coinTextView.getText().toString()).getSymbol(),
                thresholdPrice, aboveOrBelow);
        notificationRepository.write(notificationStorable);

    }

    private void swapProfileView() {
        NewProfileActivity mActivity = (NewProfileActivity) getActivity();
        FragmentManager fm = mActivity.getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(R.id.content_frame, new ProfileFragment()).addToBackStack("coinlist").commit();
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
                myView.findViewById(R.id.notification_edit_coin);
        textView.setAdapter(adapter);
        textView.setValidator(new AutoCompleteValidator());
        textView.setOnFocusChangeListener(new AutoCompleteFocusListener());

    }

    class AutoCompleteValidator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence text) {
            if (coinInfoHashMap.containsKey(text.toString())) {
                TextView currentPrice = myView.findViewById(R.id.coin_price);
                currentPrice.setText(Double.toString(coinInfoHashMap.get(text.toString()).getCurrentPrice()));
                EditText boughtPrice = myView.findViewById(R.id.threshold_price);
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
            if (v.getId() == R.id.notification_edit_coin && !hasFocus) {
                Log.v("Test", "Performing validation");
                ((AutoCompleteTextView)v).performValidation();
            }
        }
    }

}
