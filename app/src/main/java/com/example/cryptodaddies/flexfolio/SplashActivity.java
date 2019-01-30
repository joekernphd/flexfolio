package com.example.cryptodaddies.flexfolio;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.cryptodaddies.flexfolio.persistence.AsyncWriter;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;
import com.example.cryptodaddies.flexfolio.splash.SpinningImage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.HashSet;
import java.util.Set;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    private static final UserRepository userRepository = new UserRepository();

    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SpinningImage.build((ImageView)findViewById(R.id.imageView2), getIntentSwitchAnimationListener(LoginActivity.class));

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

    }


    private Animation.AnimationListener getIntentSwitchAnimationListener(final Class cls) {
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Background color from light to dark.
                // Look at @drawable/transition.xml
                // Look at activity_splash.xml
//                TransitionDrawable transition = (TransitionDrawable) findViewById(R.id.bgTrans).getBackground();
//                transition.startTransition(500);


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(account == null) {
                    Intent intent = new Intent(SplashActivity.this, cls);
                    startActivity(intent);
                } else {
                    // Create new intent
                    Intent intent = new Intent(SplashActivity.this, NewProfileActivity.class);
                    // Grab google email and store in string
                    String userEmail = account.getEmail();
                    // Send userEmail over intent which will route to ProfileActivity
                    intent.putExtra("userEmail", userEmail); // if its string type
                    // I guess allow network communications
                    allowNetworkCommunications();
                    // Create new user if it does not exist in the database
                    String displayName = createUserIfNotExists(userEmail);
                    intent.putExtra("displayName", displayName);
                    //can probably do tyhis cleaner by integrating getting currency into the createuserifnotexists function
                    UserStorable userStorable = userRepository.read(userEmail);
                    String currency = userStorable.getCurrency();
                    intent.putExtra("currency", currency);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    // Creates user if user does not already exist in the database "users" table
    private String createUserIfNotExists(String userEmail) {
        // Initialize displayName
        String displayName;

        if(userRepository.read(userEmail) == null) {
            // Creates display name from google email address
            displayName = userEmail.split("\\@")[0];
            Set<String> follows = new HashSet<String>();
            follows.add(userEmail);
            userRepository.write(new UserStorable(userEmail, displayName, follows, "USD", false));

        }

        else {
            UserStorable userStorable = userRepository.read(userEmail);
            displayName = userStorable.getDisplayName();
        }

        return displayName;

    }

    // Allow network communications
    private void allowNetworkCommunications() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
