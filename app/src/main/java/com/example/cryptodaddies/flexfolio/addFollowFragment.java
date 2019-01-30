package com.example.cryptodaddies.flexfolio;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;

import java.util.Set;


/**
 * Created by xhonifilo on 2/3/18.
 */

public class addFollowFragment extends Fragment implements View.OnClickListener {
    View myView;
    UserRepository userRepository = new UserRepository();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.add_follow_layout, container, false);
        Button b = myView.findViewById(R.id.followButton);
        b.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onClick(View view) {
        int targetid = view.getId();
        switch (targetid) {
            case R.id.followButton:
                EditText userInput = myView.findViewById(R.id.email_input);
                UserStorable toFollow = userRepository.read(userInput.getText().toString());
                //user exists
                if(toFollow != null) {
                    //user isnt private
                    if(toFollow.getPrivacy() == false) {
                        //get current logged in user
                        NewProfileActivity mActivity = (NewProfileActivity) getActivity();
                        UserStorable currentUser = userRepository.read(mActivity.getUserEmail());

                        //add to follow list
                        Set<String> follows = currentUser.getFollows();
                        follows.add(toFollow.getEmail());
                        for(String s : follows){
                            System.out.println("esketit " + s);
                        }
                        currentUser.setFollows(follows);
                        userRepository.write(currentUser);

                        //notify user
                        String toToast = "You are now following " + toFollow.getDisplayName();
                        Toast.makeText(getActivity(),toToast, Toast.LENGTH_LONG).show();
                    }
                    //user is private
                    else {
                        Toast.makeText(getActivity(),"Error: User is private.", Toast.LENGTH_LONG).show();
                    }
                }
                //user does not exist
                else {
                    Toast.makeText(getActivity(),"Error: User does not exist.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
