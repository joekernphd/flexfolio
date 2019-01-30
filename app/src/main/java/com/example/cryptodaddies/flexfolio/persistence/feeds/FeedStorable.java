package com.example.cryptodaddies.flexfolio.persistence.feeds;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raymond on 10/22/2017.
 * Used as the storable to send and receive data from the users database in DynamoDB
 */

@DynamoDBTable(tableName = "feeds")
public class FeedStorable {
    private String email;
    private String status;
    private Long time;

    public FeedStorable() { }

    public FeedStorable(String email, String status, Long time) {
        this.email = email;
        this.status = status;
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

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, AttributeValue> generateMapItem() {
        Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();

        // map.put("feedId", new AttributeValue(feedId));
        map.put("email", new AttributeValue(email));
        // map.put("displayName", new AttributeValue(displayName));
        map.put("status", new AttributeValue(status));
        map.put("status", new AttributeValue().withN(Long.toString(time)));

        return map;
    }

    @Override
    public String toString() {
        return "FeedStorable{" +
                "email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedStorable that = (FeedStorable) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return time != null ? time.equals(that.time) : that.time == null;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
