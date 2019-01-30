package com.example.cryptodaddies.flexfolio.persistence.investments;

/**
 * Created by Raymond on 2/10/2018.
 */

public final class InvestmentStorableFactory {
    private InvestmentStorableFactory() {throw new AssertionError("Dont instantiate.");}

    public static InvestmentStorable build(String email, String coin, Double amountBought, Double priceBought) {
        return new InvestmentStorable( email,
                coin,
                amountBought,
                priceBought,
                System.currentTimeMillis());
    }

    public static InvestmentStorable build(String email, String coin, String amountBought, String priceBought) {
        return new InvestmentStorable(email,
                coin,
                Double.valueOf(amountBought),
                Double.valueOf(priceBought),
                System.currentTimeMillis());
    }

}
