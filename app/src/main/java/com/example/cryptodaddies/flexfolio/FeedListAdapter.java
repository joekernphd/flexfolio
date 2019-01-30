package com.example.cryptodaddies.flexfolio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedStorable;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by xhonifilo on 2/15/18.
 */

public class FeedListAdapter extends ArrayAdapter<FeedStorable> {

    private static final UserRepository userRepository = new UserRepository();

    private static HashMap<String, String> emailDisplayMap = new HashMap<>();

    public FeedListAdapter(@NonNull Context context, ArrayList<FeedStorable> feedStorable) {
        super(context, R.layout.feedlist_row, feedStorable);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater feedInflater = LayoutInflater.from(getContext());
        View feedView;

        FeedStorable feedEntry =  getItem(position);


        String feedrowDisplayName;

        if (emailDisplayMap.containsKey(feedEntry.getEmail())) {
            feedrowDisplayName = emailDisplayMap.get(feedEntry.getEmail());
        } else {
            feedrowDisplayName = userRepository.read(feedEntry.getEmail()).getDisplayName();
            emailDisplayMap.put(feedEntry.getEmail(), feedrowDisplayName);
        }

        // IF ELSE used to switch layouts depending on item position
        if(position % 2 == 1) { feedView = feedInflater.inflate(R.layout.feedlist_row, parent, false); }
        else { feedView = feedInflater.inflate(R.layout.feedlist_row1, parent, false); }


        // Get the current epoch time and get the current date
        String currentDayDate = GetHumanReadableDate(System.currentTimeMillis(), "MM-dd-yyyy");

        // Grabs the entry time and converts into date
        String entryDate = GetHumanReadableDate(feedEntry.getTime(), "MM-dd-yyyy");

        String feedrowTime;
        // if the entry date is the same as the currentdate
        if(currentDayDate.equals(entryDate)) {
            feedrowTime = GetHumanReadableDate(feedEntry.getTime(), "h:mm a");
        }

        else {
            feedrowTime = entryDate;
        }

        TextView feedDisplayName = (TextView) feedView.findViewById(R.id.feedrow_displayName);
        TextView feedStatus = (TextView) feedView.findViewById(R.id.feedrow_status);
        TextView feedTime= (TextView) feedView.findViewById(R.id.feedrow_time);
        TextView feedInitials = (TextView) feedView.findViewById(R.id.feedrow_initials);

        String feedrowEmail = feedEntry.getEmail();
        String feedrowStatus = feedEntry.getStatus();
        String feedrowInitials = feedrowEmail.substring(0, 2);

        feedDisplayName.setText(feedrowDisplayName);
        feedStatus.setText(feedrowStatus);
        feedTime.setText(feedrowTime);
        feedInitials.setText(feedrowInitials);

        return feedView;
    }

    public static String GetHumanReadableDate(long epochSec, String dateFormatStr) {
        Date date = new Date(epochSec);
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStr,
                Locale.getDefault());
        return format.format(date);
    }
}
