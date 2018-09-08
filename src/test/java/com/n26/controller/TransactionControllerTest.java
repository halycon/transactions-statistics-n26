package com.n26.controller;

import com.n26.domain.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void transcations_withRandomTransactionBody_returnsCREATED() {

        Transaction transaction = new Transaction(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP).toString(),
                Instant.now());

        ResponseEntity<Void> response = testRestTemplate.postForEntity("/transactions", transaction, Void.class);
        Assert.assertEquals(" response code is not CREATED ", HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void transcations_post_withRandomTransactionBody_returnsNO_CONTENT() {

        Transaction transaction = new Transaction(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP).toString(),
                Instant.now().minus(120, ChronoUnit.SECONDS));

        ResponseEntity<Void> response = testRestTemplate.postForEntity("/transactions", transaction, Void.class);
        Assert.assertEquals(" response code is not NO_CONTENT ", HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void transcations_delete_withRandomTransactionBody_returnsNO_CONTENT() {

        Transaction transaction = new Transaction(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP).toString(),
                Instant.now().minus(120, ChronoUnit.SECONDS));

        ResponseEntity<Void> response = testRestTemplate.exchange("/transactions", HttpMethod.DELETE, null, Void.class);
        Assert.assertEquals(" response code is not NO_CONTENT ", HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void transcations_post_withWrongBodyType_returnsBAD_REQUEST() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ResponseEntity<Void> response = testRestTemplate.exchange("/transactions", HttpMethod.POST,
                new HttpEntity<>("Test", headers), Void.class);
        Assert.assertEquals(" response code is not BAD_REQUEST ", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
