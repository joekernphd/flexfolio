package com.example.cryptodaddies.flexfolio.persistence.investments;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raymond on 10/22/2017.
 * Used as the storable to send and receive data from the users database in DynamoDB
 */

@DynamoDBTable(tableName = "investments")
public class InvestmentStorable {
    private String email;
    private Long time;
    private String coin;
    private Double amountBought;
    private Double priceBought;

    public InvestmentStorable() { }

    public InvestmentStorable(String email, String coin, Double amountBought, Double priceBought, Long time) {
        this.email = email;
        this.coin = coin;
        this.amountBought = amountBought;
        this.priceBought = priceBought;
        this.time = time;
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

    @DynamoDBAttribute(attributeName = "amountBought")
    public Double getAmountBought() {
        return amountBought;
    }

    public void setAmountBought(Double amountBought) {
        this.amountBought = amountBought;
    }

    @DynamoDBAttribute(attributeName = "priceBought")
    public Double getPriceBought() {
        return priceBought;
    }

    public void setPriceBought(Double priceBought) {
        this.priceBought = priceBought;
    }

    public Map<String, AttributeValue> generateMapItem() {
        Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();

        map.put("email", new AttributeValue(email));
        map.put("coin", new AttributeValue(coin));
        map.put("time", new AttributeValue().withN(Long.toString(time)));

        return map;
    }

    @Override
    public String toString() {
        return "InvestmentStorable{" +
                "email='" + email + '\'' +
                ", coin='" + coin + '\'' +
                ", amountBought=" + amountBought +
                ", priceBought=" + priceBought +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvestmentStorable that = (InvestmentStorable) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (coin != null ? !coin.equals(that.coin) : that.coin != null) return false;
        if (amountBought != null ? !amountBought.equals(that.amountBought) : that.amountBought != null)
            return false;
        if (priceBought != null ? !priceBought.equals(that.priceBought) : that.priceBought != null)
            return false;
        return time != null ? time.equals(that.time) : that.time == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (coin != null ? coin.hashCode() : 0);
        result = 31 * result + (amountBought != null ? amountBought.hashCode() : 0);
        result = 31 * result + (priceBought != null ? priceBought.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
