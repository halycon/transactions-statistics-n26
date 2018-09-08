package com.n26.repository;

import com.n26.domain.Transaction;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository {

    void save(Transaction transaction);

    void deleteAll();

    List<Transaction> findAllBetweenTimestamps(Instant from, Instant to);

    List<Transaction> findTransactionsByInstant(Instant instant);
}
