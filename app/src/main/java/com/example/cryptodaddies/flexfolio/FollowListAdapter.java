package com.example.cryptodaddies.flexfolio;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

import java.util.ArrayList;

/**
 * Created by xhonifilo on 2/15/18.
 */

public class FollowListAdapter extends ArrayAdapter<UserStorable> {

    public FollowListAdapter(@NonNull Context context, ArrayList<UserStorable> userStorable) {
        super(context, R.layout.followlist_row, userStorable);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater followInflater = LayoutInflater.from(getContext());
        View followView = followInflater.inflate(R.layout.followlist_row, parent, false);

        UserStorable followEntry =  getItem(position);
        TextView followEmail = (TextView) followView.findViewById(R.id.followrow_displayName);
        TextView followDisplayName = (TextView) followView.findViewById(R.id.followrow_email);
        TextView followInitials = (TextView) followView.findViewById(R.id.followrow_initials);

        String followrowEmail = followEntry.getEmail();
        String followrowInitials = followrowEmail.substring(0, 2);

        followEmail.setText(followrowEmail);
        followDisplayName.setText(followEntry.getDisplayName());
        followInitials.setText(followrowInitials);

        followView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (NewProfileActivity)parent.getContext();
                changeFragmentFromAdapter(activity, R.id.content_frame, followrowEmail);
            }
        });

        followView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                NewProfileActivity activity = (NewProfileActivity) parent.getContext();
                activity.showStopFollowMenu(followrowEmail);
                return true;
            }
        });

        return followView;
    }

    public void changeFragmentFromAdapter(Activity act, int layoutid, String email) {

        FriendChartFragment friendChartFragment = new FriendChartFragment();

        friendChartFragment.setData(email);

        FragmentManager fragmentManager = act.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(layoutid, friendChartFragment).addToBackStack("followlist").commit();
    }



}
