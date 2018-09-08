package com.n26.service.impl;

import com.n26.domain.Transaction;
import com.n26.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service("TransactionService")
public class TransactionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "TransactionInMemoryRepository")
    private TransactionRepository transactionRepository;

    public boolean validateForOlderTransactionTimestamp(Transaction transaction, Instant now){
        return transaction.getTimestamp().isAfter(now.minus(60,ChronoUnit.SECONDS));
    }

    public boolean validateForFutureTransactionTimestampAndAmount(Transaction transaction, Instant now){
        return !transaction.getTimestamp().isAfter(now) && validateTransactionAmount(transaction);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public void removeTransactions() {
        transactionRepository.deleteAll();
    }

    private boolean validateTransactionAmount(Transaction transaction){
        try {
            transaction.setAmountDecimal(new BigDecimal(transaction.getAmount()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
