package com.teya.tinyledger.repository;

import com.teya.tinyledger.model.Transaction;

import java.util.Collection;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Collection<Transaction> findAll();
}
