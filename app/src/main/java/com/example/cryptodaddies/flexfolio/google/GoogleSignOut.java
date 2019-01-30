package com.example.cryptodaddies.flexfolio.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.cryptodaddies.flexfolio.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Raymond on 2/1/2018.
 */

public class GoogleSignOut {
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity activity;

    public GoogleSignOut(Activity activity) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        this.activity = activity;
    }

    public void signOut(Context appContext) {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        appContext.startActivity(new Intent(activity, LoginActivity.class));
                    }
                });
    }
}
