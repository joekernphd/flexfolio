package com.example.cryptodaddies.flexfolio.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.example.cryptodaddies.flexfolio.persistence.feeds.FeedRepository;
import com.example.cryptodaddies.flexfolio.persistence.investments.InvestmentRepository;
import com.example.cryptodaddies.flexfolio.persistence.users.UserRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raymond on 11/15/2017.
 * Before using this class, ensure that no entries are null, or that the generatemapitems function accounts for that
 */

public final class AsyncWriter {
    private static final AmazonDynamoDBAsyncClient asyncClient = new AmazonDynamoDBAsyncClient(CredentialsForAWS.getAWSCredentials());
    private static final Set<String> legalTableNames = legalTableNames();

    private AsyncWriter() {
        throw new AssertionError("Dont instantiate this!");
    }

    public static void write(String tableName, Map<String, AttributeValue> storableMap) {
        if(legalTableNames.contains(tableName)) {
            asyncClient.putItemAsync(new PutItemRequest(tableName, storableMap));
        } else {
            throw new AssertionError("Not a legal table name!");
        }
    }

    private static Set<String> legalTableNames() {
        Set<String> legalTableNames = new HashSet<String>();
        legalTableNames.add(UserRepository.getTableName());
        legalTableNames.add(FeedRepository.getTableName());
        legalTableNames.add(InvestmentRepository.getTableName());
        return legalTableNames;
    }
}
