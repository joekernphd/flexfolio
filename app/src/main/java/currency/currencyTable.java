package currency;

import java.util.HashMap;

/**
 * Created by Raymond on 2/25/2018.
 */

public final class currencyTable {
    private currencyTable() {
        throw new AssertionError("Dont instantiate");
    }
    private static final Double USD_TO_EUR = 0.81;
    private static final Double USD_TO_GBP = 0.71;
    private static final Double USD_TO_BTC = 0.000103;
    private static final Double USD_TO_ETH = 0.001169;

    private static HashMap<String, Double> usdConversionTable = initUsdConversionTable();
    private static HashMap<String, Double> initUsdConversionTable() {
        HashMap<String, Double> map = new HashMap<>();
        map.put("USD", 1.0);
        map.put("BTC", USD_TO_BTC);
        map.put("ETH", USD_TO_ETH);
        map.put("GBP", USD_TO_GBP);
        map.put("EUR", USD_TO_EUR);

        return map;
    }

    public static Double getUsdConversion(String currency) {
        return usdConversionTable.get(currency);
    }

    public static void updateBTC(Double value) {
        usdConversionTable.put("BTC", value);
    }

    public static void updateETH(Double value) {
        usdConversionTable.put("ETH", value);
    }
}
