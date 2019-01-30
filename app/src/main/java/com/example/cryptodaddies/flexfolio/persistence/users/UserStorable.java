package com.example.cryptodaddies.flexfolio.persistence.users;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raymond on 10/22/2017.
 * Used as the storable to send and receive data from the users database in DynamoDB
 */

@DynamoDBTable(tableName = "users")
public class UserStorable {
    private String email;
    private String displayName;
    private Set<String> follows;
    private String currency;
    private boolean isPrivate;

    public UserStorable() {

    }

    public UserStorable(String email, String displayName, Set<String> follows, String currency, boolean isPrivate) {
        this.email = email;
        this.displayName = displayName;
        this.follows = follows;
        this.currency = currency;
        this.isPrivate = isPrivate;
    }

    @DynamoDBHashKey(attributeName = "email")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "displayName")
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @DynamoDBAttribute(attributeName = "follows")
    public Set<String> getFollows() {
        return follows;
    }
    public void setFollows(Set<String> follows) {
        this.follows = follows;
    }

    @DynamoDBAttribute(attributeName = "currency")
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @DynamoDBAttribute(attributeName = "isPrivate")
    public boolean getPrivacy() { return isPrivate; }
    public void setPrivacy(boolean isPrivate) { this.isPrivate = isPrivate; }

    public Map<String, AttributeValue> generateMapItem() {
        Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();

        map.put("email", new AttributeValue(email));
        map.put("displayName", new AttributeValue(displayName));
        if(follows != null && !follows.isEmpty()) {
            map.put("follows", new AttributeValue().withSS(follows));
        }
        map.put("currency", new AttributeValue(currency));
        map.put("isPrivate", new AttributeValue(String.valueOf(isPrivate)));

        return map;
    }

    @Override
    public String toString() {
        return "UserStorable{" +
                "email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", follows=" + follows +
                ", currency='" + currency + '\'' +
                ", privacy=" + String.valueOf(isPrivate) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserStorable that = (UserStorable) o;

        if (isPrivate != that.isPrivate) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null)
            return false;
        if (follows != null ? !follows.equals(that.follows) : that.follows != null) return false;
        return currency != null ? currency.equals(that.currency) : that.currency == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (follows != null ? follows.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (isPrivate ? 1 : 0);
        return result;
    }
}
