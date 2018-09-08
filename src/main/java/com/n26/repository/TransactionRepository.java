package com.n26.repository;

import com.n26.domain.Transaction;

public interface TransactionRepository {

    void save(Transaction transaction);
    void deleteAll();
}
