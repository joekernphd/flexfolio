package com.example.cryptodaddies.flexfolio;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.cryptodaddies.flexfolio.persistence.AsyncWriter;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserStorable;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final UserRepository userRepository = new UserRepository();

    // Google Login API client
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpGoogleSignInButton();
        setUpSignOutButton();
        setUpTestButton();
        allowNetworkCommunications();
    }

    private void setUpTestButton() {
//        Button testButton = (Button) findViewById(R.id.test_button);
//        testButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("Test");
//                UserRepository repo = new UserRepository();
//                repo.write(new UserStorable("dank", "memes", null));
//
//                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void setUpSignOutButton() {
//        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
//        signOutButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signOut();
//            }
//        });
    }

    private void setUpGoogleSignInButton() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        googleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void allowNetworkCommunications() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText(LoginActivity.this,
                                "Logging out",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Google sign in logic
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String userEmail = account.getEmail();

            // Signed in successfully, show authenticated UI.
            updateUiForSignIn(userEmail);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUiForSignIn(null);
        }
    }

    private void updateUiForSignIn(String userEmail) {
        if(userEmail != null) {

            // Create user in DB if user not already created
            String displayName = createUserIfNotExists(userEmail);

            Intent intent = new Intent(LoginActivity.this, NewProfileActivity.class);
            intent.putExtra("userEmail", userEmail);// if its string type
            intent.putExtra("displayName", displayName);
            UserStorable userStorable = userRepository.read(userEmail);
            String currency = userStorable.getCurrency();
            intent.putExtra("currency", currency);
            startActivity(intent);
        } else {
            Toast.makeText(LoginActivity.this,
                    "Sign in failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // When user hits sign in button, function will use email to check whether user is registered.
    // If user is not registered, will create user in DB and will use email as displayname.
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {

    }

}

