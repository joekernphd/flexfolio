package com.example.cryptodaddies.flexfolio.persistence;

import com.amazonaws.auth.BasicAWSCredentials;

/**
 * Created by Raymond on 10/22/2017.
 * Has our aws credentials
 */

public final class CredentialsForAWS {
    private static final BasicAWSCredentials credentials =
            new BasicAWSCredentials("",
                    "");;

    private CredentialsForAWS() {
        throw new AssertionError("This class is not instantiable");
    }

    public static BasicAWSCredentials getAWSCredentials() {
        return credentials;
    }
}
