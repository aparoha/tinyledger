package com.teya.tinyledger.strategy;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.TransactionEntry;

public interface BalanceStrategy {
    double calculateNewBalance(Account account, TransactionEntry entry);
}
