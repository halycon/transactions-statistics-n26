package com.n26.service;

import com.n26.domain.Transaction;
import com.n26.repository.TransactionRepository;
import com.n26.repository.impl.TransactionInMemoryRepository;
import com.n26.service.impl.TransactionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
public class TransactionServiceTest {

    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @Before
    public void init() {
        transactionService = new TransactionService();
        transactionRepository = new TransactionInMemoryRepository();
        transactionService.setTransactionRepository(transactionRepository);
    }

    @Test
    public void should_SaveRandomTransactionByServiceAndFetchItFromRepository_Successfully() {
        Instant now = Instant.now();
        transactionService.saveTransaction(new Transaction(BigDecimal.valueOf(200), now));
        transactionService.saveTransaction(new Transaction(BigDecimal.valueOf(203), now));
        List<Transaction> transactionList = transactionService.findTransactionsByInstant(now);
        Assert.assertNotNull(" transaction list is null", transactionList);
        Assert.assertEquals("saved amount is not 200", transactionList.get(0).getAmountDecimal(),
                BigDecimal.valueOf(200).setScale(2, BigDecimal.ROUND_HALF_UP));
        Assert.assertEquals("saved amount is not 203", transactionList.get(1).getAmountDecimal(),
                BigDecimal.valueOf(203).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void should_DeleteTransactions_Successfully() {
        Instant now = Instant.now();
        transactionService.saveTransaction(new Transaction(BigDecimal.valueOf(200), now));
        transactionService.saveTransaction(new Transaction(BigDecimal.valueOf(203), now));
        transactionService.removeTransactions();
        List<Transaction> transactionList = transactionService.findTransactionsByInstant(now);
        Assert.assertNull(" transaction list is not null", transactionList);
    }

    @Test
    public void validateForOlderTransactionTimestamp_ParamOlderThan60Sec_returnsFalse() {
        Instant now = Instant.now();
        Instant sixtyOneSecondBefore = now.minus(61, ChronoUnit.SECONDS);
        Transaction transaction = new Transaction(BigDecimal.valueOf(200), sixtyOneSecondBefore);
        boolean validationResponse = transactionService.validateForOlderTransactionTimestamp(transaction, now);
        Assert.assertFalse(" timestamp is not older than 60 seconds", validationResponse);
    }

    @Test
    public void validateForOlderTransactionTimestamp_ParamIn60SecRange_returnsTrue() {
        Instant now = Instant.now();
        Instant sixtyOneSecondBefore = now.minus(59, ChronoUnit.SECONDS);
        Transaction transaction = new Transaction(BigDecimal.valueOf(200), sixtyOneSecondBefore);
        boolean validationResponse = transactionService.validateForOlderTransactionTimestamp(transaction, now);
        Assert.assertTrue(" timestamp is older than 60 seconds", validationResponse);
    }

    @Test
    public void validateForFutureTransactionTimestamp_ParamIn60SecRange_returnsTrue() {
        Instant now = Instant.now();
        Transaction transaction = new Transaction(BigDecimal.valueOf(200), now);
        boolean validationResponse = transactionService.validateForFutureTransactionTimestamp(transaction, now);
        Assert.assertTrue(" transaction is in future", validationResponse);
    }

    @Test
    public void validateForFutureTransactionTimestamp_ParamInFuture_returnsFalse() {
        Instant now = Instant.now();
        Transaction transaction = new Transaction(BigDecimal.valueOf(200), now.plus(5, ChronoUnit.SECONDS));
        boolean validationResponse = transactionService.validateForFutureTransactionTimestamp(transaction, now);
        Assert.assertFalse(" transaction is not in future", validationResponse);
    }

    @Test
    public void validateTransactionAmount_param200_returnsTrue() {

        Transaction transaction = new Transaction();
        transaction.setAmount("200");
        boolean validationResponse = transactionService.validateTransactionAmount(transaction);
        Assert.assertTrue(" invalid big decimal amount ", validationResponse);
    }

    @Test
    public void validateTransactionAmount_paramTest_returnsFalse() {

        Transaction transaction = new Transaction();
        transaction.setAmount("Test");
        boolean validationResponse = transactionService.validateTransactionAmount(transaction);
        Assert.assertFalse(" valid big decimal amount ", validationResponse);
    }
}
