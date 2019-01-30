package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.graphing.FriendPieChartFragment;
import com.example.cryptodaddies.flexfolio.graphing.PieChartFragment;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

/**
 * Created by joe on 3/3/18.
 */

public class FriendChartFragment extends Fragment {
    private View myView;
    protected String email;
    UserRepository userRepo = new UserRepository();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.friendchart_layout, container, false);

        UserStorable friend = userRepo.read(email);
        TextView friendDisplayName = myView.findViewById(R.id.friend_name);
        friendDisplayName.setText(friend.getDisplayName());

        ((NewProfileActivity) getActivity()).setFriendEmail(friend.getEmail());

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_in_right, R.animator.slide_out_right)
                .replace(R.id.friend_chart_frame, new FriendPieChartFragment(), "FriendChart").commit();

        return myView;
    }

    public void setData(String email) {
        this.email = email;
    }
}
