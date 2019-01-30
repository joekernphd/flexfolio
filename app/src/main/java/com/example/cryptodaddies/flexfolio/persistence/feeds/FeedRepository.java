package com.example.cryptodaddies.flexfolio.persistence.feeds;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.cryptodaddies.flexfolio.persistence.CredentialsForAWS;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentStorable;

/**
 * Created by Raymond on 10/22/2017.
 * Used to handle the communication itself with DynamoDB users table
 */

public class FeedRepository {
    private static final String TABLE_NAME = "feeds";
    private final AmazonDynamoDBClient ddbClient;
    private final DynamoDBMapper mapper;

    public FeedRepository() {
        ddbClient = new AmazonDynamoDBClient(CredentialsForAWS.getAWSCredentials());
        mapper = new DynamoDBMapper(ddbClient);
    }

    public FeedStorable read(FeedStorable storable) {
        return mapper.load(FeedStorable.class, storable.getEmail());
    }

    public FeedStorable read(String email) {
        return mapper.load(FeedStorable.class, email);
    }

    public void write(FeedStorable storable) {
        mapper.save(storable);
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public PaginatedList<FeedStorable> getFeedsQuery(String email) {
        DynamoDBQueryExpression<FeedStorable> queryExpression = new DynamoDBQueryExpression<FeedStorable>()
                .withHashKeyValues(new FeedStorable(email, null, null));

        return mapper.query(FeedStorable.class, queryExpression);
    }
}
