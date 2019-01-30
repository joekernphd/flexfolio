package com.example.cryptodaddies.flexfolio.api;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ronsoncookie on 2/6/2018.
 */

public class ChartInfo {
    private Long openTime;
    private Long closeTime;
    private Float open;
    private Float close;
    private Float high;
    private Float low;

    public ChartInfo(Long openTime, Long closeTime, Float open, Float close, Float high, Float low) {
        super();
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;

    }

    public Float getOpen() {
        return open;
    }

    public Float getClose() {
        return close;
    }

    public Float getHigh() {
        return high;
    }

    public Float getLow() {
        return low;
    }

    public String getOpenTime() {
        Date date = new Date(openTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return new DateFormatSymbols().getMonths()[month-1];
//        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
//        format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    }

    public String getCloseTime() {
        Date date = new Date(closeTime);
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat month_date = new SimpleDateFormat("MMM.dd");


        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month_date.format(cal.getTime());
    }



}
