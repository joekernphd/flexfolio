package com.example.cryptodaddies.flexfolio.persistence.notifications;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.cryptodaddies.flexfolio.api.CoinInvestment;
import com.example.cryptodaddies.flexfolio.graphing.PieChartData;
import com.example.cryptodaddies.flexfolio.persistence.CredentialsForAWS;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raymond on 3/3/2018.
 */

public class NotificationRepository {
    private static final String TABLE_NAME = "notifications";
    private static final AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(CredentialsForAWS.getAWSCredentials());;
    private static final DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

    public static NotificationStorable read(NotificationStorable storable) {
        return mapper.load(NotificationStorable.class, storable.getEmail(), storable.getTime());
    }

    public static NotificationStorable read(String email, Long time) {
        return mapper.load(NotificationStorable.class, email, time);
    }

    public static void write(NotificationStorable storable) {
        mapper.save(storable);
    }

    public static PaginatedList<NotificationStorable> getNotificationsQuery(String email) {
        DynamoDBQueryExpression<NotificationStorable> queryExpression = new DynamoDBQueryExpression<NotificationStorable>()
                .withHashKeyValues(NotificationStorableFactory.build(email, null, null, null));

        return mapper.query(NotificationStorable.class, queryExpression);
    }

    public static void delete(NotificationStorable n) {
        mapper.delete(n);
    }

    public static String getTableName() {
        return TABLE_NAME;
    }
}
