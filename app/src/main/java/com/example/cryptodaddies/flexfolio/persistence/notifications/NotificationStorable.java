package com.example.cryptodaddies.flexfolio.persistence.notifications;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Raymond on 3/3/2018.
 */

@DynamoDBTable(tableName = "notifications")
public class NotificationStorable {
    private String email;
    private Long time;
    private String coin;
    private Double priceThreshold;
    private Integer priceAboveOrBelow;

    public static final Integer ABOVE = 1;
    public static final Integer BELOW = 0;

    public NotificationStorable() {
    }

    public NotificationStorable(String email, Long time, String coin, Double priceThreshold, Integer priceAboveOrBelow) {
        this.email = email;
        this.time = time;
        this.coin = coin;
        this.priceThreshold = priceThreshold;
        this.priceAboveOrBelow = priceAboveOrBelow;
    }

    @DynamoDBHashKey(attributeName = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBRangeKey(attributeName = "time")
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @DynamoDBAttribute(attributeName = "coin")
    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    @DynamoDBAttribute(attributeName = "priceThreshold")
    public Double getPriceThreshold() {
        return priceThreshold;
    }

    public void setPriceThreshold(Double priceThreshold) {
        this.priceThreshold = priceThreshold;
    }

    @Override
    public String toString() {
        return "NotificationStorable{" +
                "email='" + email + '\'' +
                ", time=" + time +
                ", coin='" + coin + '\'' +
                ", priceThreshold=" + priceThreshold +
                ", priceAboveOrBelow=" + priceAboveOrBelow +
                '}';
    }

    @DynamoDBAttribute(attributeName = "priceAboveOrBelow")
    public Integer getPriceAboveOrBelow() {
        return priceAboveOrBelow;
    }

    public void setPriceAboveOrBelow(Integer priceAboveOrBelow) {
        this.priceAboveOrBelow = priceAboveOrBelow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationStorable that = (NotificationStorable) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (coin != null ? !coin.equals(that.coin) : that.coin != null) return false;
        if (priceThreshold != null ? !priceThreshold.equals(that.priceThreshold) : that.priceThreshold != null)
            return false;
        return priceAboveOrBelow != null ? priceAboveOrBelow.equals(that.priceAboveOrBelow) : that.priceAboveOrBelow == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (coin != null ? coin.hashCode() : 0);
        result = 31 * result + (priceThreshold != null ? priceThreshold.hashCode() : 0);
        result = 31 * result + (priceAboveOrBelow != null ? priceAboveOrBelow.hashCode() : 0);
        return result;
    }
}
