package com.n26.repository.impl;

import com.n26.domain.Transaction;
import com.n26.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.ref.SoftReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Repository("TransactionInMemoryRepository")
public class TransactionInMemoryRepository implements TransactionRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentNavigableMap<Long, SoftReference<List<Transaction>>> transactionMap = new ConcurrentSkipListMap<>();
    private final DelayQueue<DelayedCacheObject> cleaningUpQueue = new DelayQueue<>();
    private Thread cleanerThread;
    private boolean cancelCleanerThread = false;
    private final long expiryMilliseconds = 60000;


    @Override
    public void save(Transaction transaction) {
        transactionMap.computeIfAbsent(transaction.getTimestamp().toEpochMilli(),
                k -> {
                    SoftReference<List<Transaction>> softReference = new SoftReference<>(Collections.synchronizedList(new ArrayList<>()));
                    cleaningUpQueue.put(new DelayedCacheObject(transaction.getTimestamp().toEpochMilli(),
                            softReference, System.currentTimeMillis() + expiryMilliseconds));
                    return softReference;
                }).get().add(transaction);

    }

    @PostConstruct
    public void init() {
        cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !cancelCleanerThread) {
                try {
                    DelayedCacheObject delayedCacheObject = cleaningUpQueue.take();
                    transactionMap.remove(delayedCacheObject.getKey(), delayedCacheObject.getReference());

                    logger.info("removing object with key {}", delayedCacheObject.getKey());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }

    @PreDestroy
    public void cleanup() {
        cancelCleanerThread = true;
    }

    @Override
    public void deleteAll() {
        cleaningUpQueue.clear();
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
                        return i.get().stream();
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findTransactionsByInstant(Instant instant) {
        SoftReference<List<Transaction>> nullable = transactionMap.get(instant.toEpochMilli());
        return nullable != null ? nullable.get() : null;

    }

    private static class DelayedCacheObject implements Delayed {

        private final Long key;
        private SoftReference<List<Transaction>> reference;
        private final long expiryTime;

        private DelayedCacheObject(Long key, SoftReference<List<Transaction>> reference, long expiryTime) {
            this.key = key;
            this.reference = reference;
            this.expiryTime = expiryTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(expiryTime, ((DelayedCacheObject) o).expiryTime);
        }

        public Long getKey() {
            return key;
        }

        public SoftReference<List<Transaction>> getReference() {
            return reference;
        }
    }
}
