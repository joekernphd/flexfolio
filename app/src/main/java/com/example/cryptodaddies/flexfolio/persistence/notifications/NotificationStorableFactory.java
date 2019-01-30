package com.example.cryptodaddies.flexfolio.persistence.notifications;

import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;

/**
 * Created by Raymond on 3/3/2018.
 */

public final class NotificationStorableFactory {
    private NotificationStorableFactory() {throw new AssertionError("Dont instantiate.");}

    public static NotificationStorable build(String email, String coin, Double priceThreshold, Integer priceAboveOrBelow) {
        return new NotificationStorable( email,
                System.currentTimeMillis(),
                coin,
                priceThreshold,
                priceAboveOrBelow);
    }
}
