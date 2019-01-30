package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedRepository;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedStorable;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by Joe on 1/31/18.
 */

public class FeedFragment extends Fragment {

    private static final UserRepository userRepository = new UserRepository();
    private static final FeedRepository feedRepository = new FeedRepository();

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.feed_layout, container, false);

        // Pulls list data
        pullListData();

        return myView;
    }

    private void pullListData() {
        // Grabs userstorable and email
        String userEmail = ((NewProfileActivity) getActivity()).getUserEmail();
        UserStorable userStorable = userRepository.read(userEmail);

        // Grabs follows from email
        Set<String> follows = userStorable.getFollows();

        // Creates empty arraylist
        ArrayList<FeedStorable> feedStorables = new ArrayList<>();

        // If currentuser has a follows list
        if(follows != null) {
            // store followed feed into ArrayList
            for (String s : follows) {
                PaginatedList<FeedStorable> temp = feedRepository.getFeedsQuery(s);
                feedStorables.addAll(temp);
            }

            // Sorts entries by time
            Collections.sort(feedStorables, new Comparator<FeedStorable>() {
                @Override
                public int compare(FeedStorable f1, FeedStorable f2) {
                    if(f1.getTime() > f2.getTime()) { return -1; }
                    else if(f1.getTime() == f2.getTime()) { return 0; }
                    else { return 1; }
                }
            });

            // Create custom adapter for feedlayout entries
            ArrayAdapter<FeedStorable> adapter = new FeedListAdapter(getContext().getApplicationContext(), feedStorables);
            ListView feedListView = (ListView) myView.findViewById(R.id.feedlist_view);
            feedListView.setAdapter(adapter);
        }

    }
}
