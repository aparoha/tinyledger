package com.teya.tinyledger.strategy;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.TransactionEntry;

public class AssetBalanceStrategy implements BalanceStrategy {
    @Override
    public double calculateNewBalance(Account account, TransactionEntry entry) {
        double current = account.getBalance();
        double amount = entry.getAmount();

        if (entry.getEntryType() == TransactionEntry.EntryType.Debit) {
            return current + amount;
        } else {
            if (amount > current) {
                throw new IllegalArgumentException("Insufficient asset balance in account ID: " + account.getId());
            }
            return current - amount;
        }
    }
}
