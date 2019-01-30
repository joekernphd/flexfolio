package com.example.cryptodaddies.flexfolio.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.example.cryptodaddies.flexfolio.R;
import com.example.cryptodaddies.flexfolio.api.CoinApi;
import com.example.cryptodaddies.flexfolio.persistence.notifications.NotificationRepository;
import com.example.cryptodaddies.flexfolio.persistence.notifications.NotificationStorable;
import com.example.cryptodaddies.flexfolio.persistence.notifications.NotificationStorableFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Raymond on 2/28/2018.
 */

public class PriceNotificationService extends IntentService {
    private static final NotificationRepository notificationRepository = new NotificationRepository();
    private NotificationManager notifManager;

    public PriceNotificationService() {
        super("PriceNotificationService");
    }
    public PriceNotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent ==  null) {
            Log.e("PriceNotificationServic", "Intent pass failed");
            return;
        }
        // Gets data from the incoming Intent
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = preferences.getString("email", null);
        PaginatedList<NotificationStorable> notifications = notificationRepository.getNotificationsQuery(email);

        CoinApi coinApi = new CoinApi(getApplicationContext());
        JsonArray coins = coinApi.simpleGetJsonArrayData(false);


        for(NotificationStorable n : notifications){
            JsonObject coin = getCoinData(coins, n.getCoin());
            String temp = coin.get("price_usd").toString();
            temp = temp.replace("\"","");
            Double currentVal = Double.parseDouble(temp);
            if(n.getPriceAboveOrBelow() == 1) { //above = 1
                if(currentVal > n.getPriceThreshold()) {
                        throwNotification(n.getCoin(), "above ", n.getPriceThreshold(), n);
                }
            }
            else { // below = 0
                if(currentVal < n.getPriceThreshold()) {
                    throwNotification(n.getCoin(), "below ", n.getPriceThreshold(), n);
                }
            }

        }

    }

    public JsonObject getCoinData(JsonArray data, String symbol) {
        String sym = "\"" + symbol + "\"";
        for (int i = 0; i < data.size(); i++) {
            JsonObject item = data.get(i).getAsJsonObject();
            String s = item.get("symbol").toString();
            if (s.equals(sym)) {
                return item;
            }
        }

        return null;
    }

    public void throwNotification(String symbol, String aboveOrBelow, Double threshold, NotificationStorable deleteMe) {
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }

            builder = new NotificationCompat.Builder(this, id);

            builder.setContentTitle(symbol + " went " + aboveOrBelow + threshold.toString())  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setTicker(symbol + " went " + aboveOrBelow + threshold.toString())
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("Price Notification");
            builder.setContentText(symbol + " went " + aboveOrBelow + threshold.toString());
            builder.setSmallIcon(R.drawable.thumbnail);
        }

        System.out.println(symbol + " went " + aboveOrBelow + threshold.toString());
        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(0, notification);

        notificationRepository.delete(deleteMe);
    }

}
