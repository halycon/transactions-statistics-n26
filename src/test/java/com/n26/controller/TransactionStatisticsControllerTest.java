package com.n26.controller;

import com.n26.domain.Transaction;
import com.n26.domain.TransactionStatistics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStatisticsControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void get_statistics_withNoParam_ReturnsTransactionStatistics() {

        testRestTemplate.delete("/transactions");

        TransactionStatistics transactionStatistics = getPredefinedStatistics();

        testRestTemplate.postForEntity("/transactions",
                new Transaction(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP).toString(), Instant.now()), Void.class);
        testRestTemplate.postForEntity("/transactions",
                new Transaction(BigDecimal.valueOf(202).setScale(2, RoundingMode.HALF_UP).toString(), Instant.now()), Void.class);
        testRestTemplate.postForEntity("/transactions",
                new Transaction(BigDecimal.valueOf(204).setScale(2, RoundingMode.HALF_UP).toString(), Instant.now()), Void.class);

        ResponseEntity<TransactionStatistics> response = testRestTemplate.getForEntity("/statistics", TransactionStatistics.class);
        Assert.assertEquals(" response code is not OK ", HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(" response body not equals to expected body ", transactionStatistics, response.getBody());

    }

    private TransactionStatistics getPredefinedStatistics() {
        TransactionStatistics transactionStatistics = new TransactionStatistics();
        transactionStatistics.setCount(3);
        transactionStatistics.setMin(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP).toString());
        transactionStatistics.setMax(BigDecimal.valueOf(204).setScale(2, RoundingMode.HALF_UP).toString());
        transactionStatistics.setAvg(BigDecimal.valueOf(202).setScale(2, RoundingMode.HALF_UP).toString());
        transactionStatistics.setSum(BigDecimal.valueOf(606).setScale(2, RoundingMode.HALF_UP).toString());
        return transactionStatistics;
    }
}
