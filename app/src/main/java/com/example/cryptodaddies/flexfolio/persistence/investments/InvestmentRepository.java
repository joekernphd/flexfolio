package com.example.cryptodaddies.flexfolio.persistence.investments;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.cryptodaddies.flexfolio.api.CoinInvestment;
import com.example.cryptodaddies.flexfolio.graphing.PieChartData;
import com.example.cryptodaddies.flexfolio.persistence.CredentialsForAWS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raymond on 10/22/2017.
 * Used to handle the communication itself with DynamoDB users table
 */

public class InvestmentRepository {
    private static final String TABLE_NAME = "investments";
    private final AmazonDynamoDBClient ddbClient;
    private final DynamoDBMapper mapper;

    public InvestmentRepository() {
        ddbClient = new AmazonDynamoDBClient(CredentialsForAWS.getAWSCredentials());
        mapper = new DynamoDBMapper(ddbClient);
    }

    public InvestmentStorable read(InvestmentStorable storable) {
        return mapper.load(InvestmentStorable.class, storable.getEmail(), storable.getTime());
    }

    public InvestmentStorable read(String email, Long time) {
        return mapper.load(InvestmentStorable.class, email, time);
    }

    public void write(InvestmentStorable storable) {
        mapper.save(storable);
    }

    public PaginatedList<InvestmentStorable> getInvestmentsQuery(String email) {
        DynamoDBQueryExpression<InvestmentStorable> queryExpression = new DynamoDBQueryExpression<InvestmentStorable>()
                .withHashKeyValues(new InvestmentStorable(email, null, null, null, null));

        return mapper.query(InvestmentStorable.class, queryExpression);
    }

    public PieChartData getInvestmentsAtCurrentValuePieChartData(String email, Map<String, Double> currentCoinValues) {
        PaginatedList<InvestmentStorable> investmentStorables = getInvestmentsQuery(email);
        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Float> yData = new ArrayList<>();
        HashMap<String, Double> map = new HashMap<>();

        for(int i = 0; i < investmentStorables.size(); i++) {
            if(map.containsKey(investmentStorables.get(i).getCoin())) {
                map.put(investmentStorables.get(i).getCoin(), map.get(investmentStorables.get(i).getCoin()) +
                        investmentStorables.get(i).getAmountBought() * currentCoinValues.get(investmentStorables.get(i).getCoin()));
            } else {
                map.put(investmentStorables.get(i).getCoin(), investmentStorables.get(i).getAmountBought() * currentCoinValues.get(investmentStorables.get(i).getCoin()));
            }
        }

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            xData.add(entry.getKey());
            yData.add(entry.getValue().floatValue());
        }

        return new PieChartData(yData, xData);
    }

    public ArrayList<CoinInvestment> getInvestmentAmounts(String email) {
        PaginatedList<InvestmentStorable> investmentStorables = getInvestmentsQuery(email);
        HashMap<String, Double> map = new HashMap<>();
        ArrayList<CoinInvestment> data = new ArrayList<>();

        for(int i = 0; i < investmentStorables.size(); i++) {
            if(map.containsKey(investmentStorables.get(i).getCoin())) {
                map.put(investmentStorables.get(i).getCoin(), map.get(investmentStorables.get(i).getCoin()) +
                        investmentStorables.get(i).getAmountBought());
            } else {
                map.put(investmentStorables.get(i).getCoin(), investmentStorables.get(i).getAmountBought());
            }
        }

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            data.add(new CoinInvestment(entry.getKey(), entry.getValue()));
        }

        return data;
    }

    //get total amount of money invested by user at buy time
    public double getCashInvested(String email) {
        PaginatedList<InvestmentStorable> investmentStorables = getInvestmentsQuery(email);
        double total = 0.0;

        for(int i = 0; i < investmentStorables.size(); i++) {
            total = total + (investmentStorables.get(i).getPriceBought() * investmentStorables.get(i).getAmountBought());
        }

        return total;
    }

    public double coinAmountInvested(String email, String symbol) {
        PaginatedList<InvestmentStorable> investmentStorables = getInvestmentsQuery(email);
        double coinAmount = 0;

        for (int i = 0; i < investmentStorables.size(); ++i) {
            if (investmentStorables.get(i).getCoin().equals(symbol)) {
                coinAmount = coinAmount + investmentStorables.get(i).getAmountBought();
            }
        }
        return coinAmount;
    }

    public void deleteInvestments(String email, String symbol) {
        PaginatedList<InvestmentStorable> investmentStorables = getInvestmentsQuery(email);

        for (int i = 0; i < investmentStorables.size(); ++i) {
            if (investmentStorables.get(i).getCoin().equals(symbol)) {
                mapper.delete(investmentStorables.get(i));
            }
        }
    }

    public static String getTableName() {
        return TABLE_NAME;
    }
}
