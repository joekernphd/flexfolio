package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

import java.util.ArrayList;
import java.util.Set;

public class FollowListFragment extends Fragment {

    private static final UserRepository userRepository = new UserRepository();

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.followlist_layout, container, false);

        // Grabs userstorable and email
        String userEmail = ((NewProfileActivity) getActivity()).getUserEmail();
        UserStorable userStorable = userRepository.read(userEmail);

        // Grabs follows from email
        Set<String> follows = userStorable.getFollows();

        // Creates empty arraylist
        ArrayList<UserStorable> userStorables = new ArrayList<>();

        // If currentuser has a follows list
        if(follows != null) {
            // store followed users into ArrayList
            for (String s : follows) {
                PaginatedList<UserStorable> temp = userRepository.getUsersQuery(s);
                userStorables.addAll(temp);
            }

            // Create custom adapter for followlist_layout entries
            ArrayAdapter<UserStorable> adapter = new FollowListAdapter(getContext().getApplicationContext(), userStorables);
            ListView followListView = (ListView) myView.findViewById(R.id.followlist_view);
            followListView.setAdapter(adapter);
        }

        setupFloatingActionButtons();

        return myView;
    }

    // Setup all floating action buttons and other buttons
    private void setupFloatingActionButtons() {
        final FloatingActionButton fabAddFollow = (FloatingActionButton) myView.findViewById(R.id.fabAddFollow);
        fabAddFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AAA", "SOMETHING");
                getActivity().getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                        .replace(R.id.content_frame, new addFollowFragment()).addToBackStack("follow_list").commit();
            }
        });
    }
}