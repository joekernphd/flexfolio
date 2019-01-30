package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.notifications.AddNotificationFragment;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

/**
 * Created by xhonifilo on 2/3/18.
 */

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final UserRepository userRepository = new UserRepository();
    private View myView;
    private String selectedCurrency;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_layout, container, false);

        // Set up the Currency Spinner
        setUpCurrencySpinner();

        // Set up button
        Button b = myView.findViewById(R.id.submitButton);
        b.setOnClickListener(this);

        return myView;
    }

    private void setUpCurrencySpinner() {
        Spinner spinner = (Spinner) myView.findViewById(R.id.currency_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        String userEmail = ((NewProfileActivity) getActivity()).getUserEmail();
        UserStorable userStorable = userRepository.read(userEmail);
        String currentCurrency = userStorable.getCurrency();

        //set privacy button too
        CheckBox privateToggle = myView.findViewById(R.id.privateButton);
        privateToggle.setChecked(userStorable.getPrivacy());

        // Grab currentCurrency_value text
        TextView currentCurrencyText = myView.findViewById(R.id.currentCurrency_value);
        currentCurrencyText.setText(currentCurrency);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.settings_currency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (currentCurrency != null) {
            int spinnerPosition = adapter.getPosition(currentCurrency);
            spinner.setSelection(spinnerPosition);
        }

        spinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Grab the value that was selected from the dropdown list
        selectedCurrency = (String) parent.getItemAtPosition(pos);

        // Changes default color of spinner text to WHITE
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
    }

    public void onNothingSelected(AdapterView<?> parent) { }
    // END SPINNER SETUP & CODE
    // END SPINNER SETUP & CODE
    // END SPINNER SETUP & CODE

    // BUTTON & SUBMISSION CODE
    // BUTTON & SUBMISSION CODE
    // BUTTON & SUBMISSION CODE

    @Override
    public void onClick(View view) {
        int targetid = view.getId();
        switch (targetid) {
            case R.id.submitButton:
                // Grab userEmail from NewprofileActivity
                String userEmail = ((NewProfileActivity) getActivity()).getUserEmail();

                // Create a new userstorable and load based on email
                UserStorable userStorable = userRepository.read(userEmail);

                // Grab displayname from display name input
                EditText editText =  myView.findViewById(R.id.edit_displayName);
                String displayNameInput = editText.getText().toString();

                // Grab currentCurrency_value text
                TextView currentCurrencyText = myView.findViewById(R.id.currentCurrency_value);

                //grab privacy
                CheckBox privateToggle = myView.findViewById(R.id.privateButton);
                userStorable.setPrivacy(privateToggle.isChecked());

                //set up activity to switch back fragment
                NewProfileActivity mActivity = (NewProfileActivity) getActivity();

                // Check length of inputted display name
                // If too long, return;
                if(displayNameInput.length() > 10) {
                    Toast.makeText(getActivity(),
                            "Displayname is too long",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // If the user selected a new currency
                if(!selectedCurrency.equals(userStorable.getCurrency())) {
                    // Store users DB currency equal to their selected currency
                    userStorable.setCurrency(selectedCurrency);
                    mActivity.setUserCurrency(selectedCurrency);
                    // Set users newly selected currency to appear on the settings page
                    currentCurrencyText.setText(selectedCurrency);
                }

                // If the user entered a new display name
                if(!displayNameInput.equals("")) {
                    // Store users DB display name equal to their input display name
                    userStorable.setDisplayName(displayNameInput);

                    // Clears editText's display name input line
                    editText.getText().clear();

                    // BEGIN UPDATING DRAWER HEADER INFO
                    NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    TextView displayNameTV = (TextView)headerView.findViewById(R.id.drawerDisplayName);
                    displayNameTV.setText(displayNameInput);
                    // END UPDATING DRAWER HEADER INFO

                }

                // Update user information in DB
                userRepository.write(userStorable);

                // Hides keyboard after clicking submit button
                ((NewProfileActivity) getActivity()).hideSoftKeyboard(getActivity());

                //notify user
                Toast.makeText(getActivity(), "Settings updated.", Toast.LENGTH_LONG).show();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                        .replace(R.id.content_frame, new ProfileFragment()).addToBackStack("portfolio").commit();

                break;
        }
    }

}
