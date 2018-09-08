package com.n26.repository.impl;

import com.n26.domain.Transaction;
import com.n26.repository.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Repository("TransactionInMemoryRepository")
public class TransactionInMemoryRepository implements TransactionRepository {

    private final ConcurrentNavigableMap<Long, List<Transaction>> transactionMap = new ConcurrentSkipListMap<>();

    @Override
    public void save(Transaction transaction) {
        transactionMap.computeIfAbsent(transaction.getTimestamp().toEpochMilli(),
                k -> Collections.synchronizedList(new ArrayList<>())).add(transaction);
    }

    @Override
    public void deleteAll() {
        transactionMap.clear();
    }

    @Override
    public List<Transaction> findAllBetweenTimestamps(Instant from, Instant to) {

        if (from.isAfter(to))
            return null;
        return transactionMap
                .subMap(from.toEpochMilli(), to.toEpochMilli())
                .values()
                .parallelStream()
                .flatMap(i -> {
                    synchronized (i) {
                        return i.stream();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findTransactionsByInstant(Instant instant) {
        return transactionMap.get(instant.toEpochMilli());
    }
}
