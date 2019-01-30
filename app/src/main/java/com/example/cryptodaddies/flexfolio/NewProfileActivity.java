package com.example.cryptodaddies.flexfolio;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.api.CoinApi;
import com.example.cryptodaddies.flexfolio.api.CoinInfo;
import com.example.cryptodaddies.flexfolio.api.CoinInvestment;
import com.example.cryptodaddies.flexfolio.google.GoogleSignOut;
import com.example.cryptodaddies.flexfolio.graphing.PieChartData;
import com.example.cryptodaddies.flexfolio.notifications.AddNotificationFragment;
import com.example.cryptodaddies.flexfolio.notifications.PriceNotificationService;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedRepository;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedStorable;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class NewProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CoinApi coinApi = new CoinApi(this);
    private GoogleSignOut googleSignOut;

    private String friendEmail;
    private String userCurrency = "USD";

    private static final UserRepository userRepository = new UserRepository();
    private static final InvestmentRepository investmentRepository = new InvestmentRepository();
    private static final FeedRepository feedRespository = new FeedRepository();

    private AlarmManager alarm;
    private PendingIntent pIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        userCurrency = getIntent().getStringExtra("currency");
        setupUI(findViewById(R.id.drawer_layout));
        googleSignOut = new GoogleSignOut(NewProfileActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                hideSoftKeyboard(NewProfileActivity.this);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(R.id.content_frame, new ProfileFragment()).addToBackStack("portfolio").commit();

        // Updates drawer displayname and email based on information based over through the intent
        getIntentDrawerInfo();


        // Store coin data after checking for network and getting the data
        if (coinApi.isNetworkAvailable()) {
            getAndSaveCoinData();
        }

        setUpNotifications();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_profile, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Do Stuff");
        if(v.getId() == R.id.followlist_view) {
            menu.clear();
            menu.add(0, v.getId(), 0, "Stop following");
        } else {
            menu.clear();
            menu.add(0, v.getId(), 0, "Delete Investment");
            menu.add(0, v.getId(), 0, "Delete Investment & Flex");
        }
//        getMenuInflater().inflate(R.menu.navigation, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle()=="Delete Investment") {
            Toast.makeText(getApplicationContext(),"Deleting Investment",Toast.LENGTH_LONG).show();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String toDelete = sharedPref.getString("investmentToDelete", "A");
            investmentRepository.deleteInvestments(getUserEmail(), toDelete);
            refresh();
        } else if(item.getTitle() == "Stop following") {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String toDelete = sharedPref.getString("userToUnfollow", null);
            if(toDelete.equals(getUserEmail())) {
                Toast.makeText(getApplicationContext(),"You can't unfollow yourself!",Toast.LENGTH_LONG).show();
            } else {
                UserStorable currentUser = userRepository.read(getUserEmail());
                Set<String> following = currentUser.getFollows();
                following.remove(toDelete);
                currentUser.setFollows(following);
                userRepository.write(currentUser);
                String toToast = "No longer following " + toDelete;
                Toast.makeText(getApplicationContext(),toToast,Toast.LENGTH_LONG).show();
                refresh();
            }
        } else if(item.getTitle() == "Delete Investment & Flex") {
            Toast.makeText(getApplicationContext(),"Flexing",Toast.LENGTH_LONG).show();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String toDelete = sharedPref.getString("investmentToDelete", "A");
            double coinAmount = investmentRepository.coinAmountInvested(getUserEmail(), toDelete);
            if(coinAmount == 0) {
                FeedStorable feedStorable = new FeedStorable(getUserEmail(), "No longer watching " + toDelete,  System.currentTimeMillis());
                feedRespository.write(feedStorable);
            }

            else {
                FeedStorable feedStorable = new FeedStorable(getUserEmail(), "No longer invested in " + toDelete,  System.currentTimeMillis());
                feedRespository.write(feedStorable);
            }

            investmentRepository.deleteInvestments(getUserEmail(), toDelete);

            refresh();
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_notification) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                    .replace(R.id.content_frame, new AddNotificationFragment()).addToBackStack("portfolio").commit();
        } else if (id == R.id.action_refresh) {
            getAndSaveCoinData();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                    .replace(R.id.content_frame, new ProfileFragment(), "profile_fragment").addToBackStack("portfolio").commit();
        } else if (id == R.id.nav_gallery) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                    .replace(R.id.content_frame, new FeedFragment()).addToBackStack("portfolio").commit();

        } else if (id == R.id.nav_slideshow) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                    .replace(R.id.content_frame, new CoinlistFragment()).addToBackStack("portfolio").commit();

        } else if (id == R.id.nav_test) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                    .replace(R.id.content_frame, new FollowListFragment()).addToBackStack("portfolio").commit();

        } else if (id == R.id.nav_manage) {
            googleSignOut.signOut(this.getApplicationContext());
            Intent intent = new Intent(NewProfileActivity.this, LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                    .replace(R.id.content_frame, new SettingsFragment()).addToBackStack("portfolio").commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getAndSaveCoinData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("cacheDataTime", System.currentTimeMillis());
        editor.commit();

        coinApi.saveData(coinApi.getAllCoinsData("USD"), "coindata");
    }

    private void getIntentDrawerInfo() {
        // Fill in drawer information from intent extra info

        // Drawer Information Fill In
        String userEmail = getIntent().getStringExtra("userEmail");
        String displayName = getIntent().getStringExtra("displayName");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView displayNameTV = (TextView)headerView.findViewById(R.id.drawerDisplayName);
        TextView userEmailTV = (TextView)headerView.findViewById(R.id.drawerEmail);

        displayNameTV.setText(displayName);
        userEmailTV.setText(userEmail);
    }

    public String getUserEmail() {
        return getIntent().getStringExtra("userEmail");
    }

    public String getUserCurrency() {
        return userCurrency;
    }
    public void setUserCurrency(String userCurrency) {
        this.userCurrency = userCurrency;
    }

    public Float getUserBalance() {
        ArrayList<Float> yData = investmentRepository.getInvestmentsAtCurrentValuePieChartData(getUserEmail(), coinApi.getCurrentCoinValueMap(false)).getyData();
        Float sum = new Float(0);
        for(Float f : yData) {
            sum += f;
        }

        return sum;
    }

    //how much money they spent. used for percentage increase/decrease
    public double getCashInvested() {
        return investmentRepository.getCashInvested(getUserEmail());
    }

    public JsonArray passCoinData() {
        // Pass coin data/info to coinlistfragment
        if (!coinApi.isNetworkAvailable()) {
            return coinApi.getData("coindata");
        }
        return coinApi.getAllCoinsData("USD");
    }

    public JsonArray passBinanceData(String coin, String currency) {
        String filename = "binance-" + coin + currency;
        if (!coinApi.isNetworkAvailable()) {

            return coinApi.getData("binance_" + coin + currency);
        }
        // store data internally
        //coinApi.saveData(coinApi.getBinanceData(coin, currency), filename);

        return coinApi.getBinanceData(coin, currency);
    }

    public ArrayList<CoinInfo> passCoinInfoArrayList(Boolean skipNetworkPull) {
        return coinApi.simpleGetData(skipNetworkPull);
    }

    public ArrayList<CoinInvestment> passCoinInvestmentArrayList(Boolean skipNetworkPull) {
        return coinApi.getInvestments(getUserEmail(), skipNetworkPull);
    }

    public CoinApi getCoinApi() {
        return coinApi;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public PieChartData getInvestments(String email) {
        return investmentRepository.getInvestmentsAtCurrentValuePieChartData(email, coinApi.getCurrentCoinValueMap(false));
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(NewProfileActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public String getFriendEmail() {
        return friendEmail;
    }
    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public void showContextMenu(String symbol) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("investmentToDelete", symbol);
        editor.commit();

        registerForContextMenu(findViewById(R.id.investmentListFragment));
        openContextMenu(findViewById(R.id.investmentListFragment));
    }

    public void showStopFollowMenu(String user) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userToUnfollow", user);
        editor.commit();

        registerForContextMenu(findViewById(R.id.followlist_view));
        openContextMenu(findViewById(R.id.followlist_view));
    }


    public void refresh() {
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).addToBackStack("portfolio").commit();
    }

    public void setUpNotifications() {
        Intent serviceIntent = new Intent(this, PriceNotificationService.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", getUserEmail());
        editor.commit();
        pIntent = PendingIntent.getService(this, UUID.randomUUID().hashCode(), serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),60000, pIntent);
    }
}

