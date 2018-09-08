package com.n26.service;

import com.n26.domain.Transaction;
import com.n26.domain.TransactionStatistics;
import com.n26.repository.TransactionRepository;
import com.n26.repository.impl.TransactionInMemoryRepository;
import com.n26.service.impl.TransactionStatisticsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest
public class TransactionStatisticsServiceTest {

    private TransactionStatisticsService transactionStatisticsStatisticsService;

    private TransactionRepository transactionRepository;

    @Before
    public void init() {
        transactionStatisticsStatisticsService = new TransactionStatisticsService();
        transactionRepository = new TransactionInMemoryRepository();
        transactionStatisticsStatisticsService.setTransactionRepository(transactionRepository);
    }

    @Test
    public void getStatisticsOfaTimePeriod_WithoutTransactions_ReturnsEmptytObject() {
        Instant now = Instant.now();

        TransactionStatistics emptyTransactionStatistics = new TransactionStatistics();

        TransactionStatistics transactionStatistics = transactionStatisticsStatisticsService.
                getStatisticsOfaTimePeriod(now.minus(1, ChronoUnit.MINUTES), now);

        Assert.assertEquals(" transaction object is not empty ", emptyTransactionStatistics, transactionStatistics);
    }

    @Test
    public void getStatisticsOfaTimePeriod_WithTransactionDataOnRepository_ReturnsCorrectObject() {
        Instant now = Instant.now();

        transactionRepository.deleteAll();
        fillRepositoryWithObjects(now);

        TransactionStatistics predefinedTransactionStatistics = new TransactionStatistics();
        predefinedTransactionStatistics.setSum("4140.00");
        predefinedTransactionStatistics.setAvg("69.00");
        predefinedTransactionStatistics.setMax("128.00");
        predefinedTransactionStatistics.setMin("10.00");
        predefinedTransactionStatistics.setCount(60);

        TransactionStatistics transactionStatistics = transactionStatisticsStatisticsService.
                getStatisticsOfaTimePeriod(now.minus(1, ChronoUnit.MINUTES), now);

        Assert.assertEquals(" transaction object values not equals to values ", predefinedTransactionStatistics,
                transactionStatistics);
    }


    private void fillRepositoryWithObjects(Instant now) {
        for (int i = 90; i >= 0; i--) {
            transactionRepository.save(new Transaction(BigDecimal.valueOf(10 + 2 * i), now.minus(1 + i, ChronoUnit.SECONDS)));
        }
    }
}
