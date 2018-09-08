package com.n26.service.impl;

import com.n26.domain.Transaction;
import com.n26.domain.TransactionStatistics;
import com.n26.repository.TransactionRepository;
import com.n26.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service("TransactionStatisticsService")
public class TransactionStatisticsService implements StatisticsService<TransactionStatistics> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "TransactionInMemoryRepository")
    private TransactionRepository transactionRepository;

    @Override
    public TransactionStatistics getStatisticsOfaTimePeriod(Instant start, Instant end) {

        List<Transaction> transactionList = transactionRepository.findAllBetweenTimestamps(start, end);

        TransactionStatistics transactionStatistics = new TransactionStatistics();

        if (transactionList == null || transactionList.isEmpty())
            return transactionStatistics;

        logger.info("transactionStatistics :: {}", transactionStatistics);
        return transactionStatistics;

    }

}
