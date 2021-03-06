package com.example.cryptodaddies.flexfolio.persistence.users;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.cryptodaddies.flexfolio.persistence.CredentialsForAWS;

/**
 * Created by Raymond on 10/22/2017.
 * Used to handle the communication itself with DynamoDB users table
 */

public class UserRepository {
    private static final String TABLE_NAME = "users";
    private final AmazonDynamoDBClient ddbClient;
    private final DynamoDBMapper mapper;

    public UserRepository() {
        ddbClient = new AmazonDynamoDBClient(CredentialsForAWS.getAWSCredentials());
        mapper = new DynamoDBMapper(ddbClient);
    }

    public UserStorable read(UserStorable storable) {
        return mapper.load(UserStorable.class, storable.getEmail());
    }
    public UserStorable read(String email) {
        return mapper.load(UserStorable.class, email);
    }

    public void write(UserStorable storable) {
        mapper.save(storable);
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public PaginatedList<UserStorable> getUsersQuery(String email) {
        DynamoDBQueryExpression<UserStorable> queryExpression = new DynamoDBQueryExpression<UserStorable>()
                .withHashKeyValues(new UserStorable(email, null, null, null, false));

        return mapper.query(UserStorable.class, queryExpression);
    }
}
