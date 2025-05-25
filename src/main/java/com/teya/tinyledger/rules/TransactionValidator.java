package com.teya.tinyledger.rules;

import com.teya.tinyledger.model.TransactionEntry;

import java.util.List;

public class TransactionValidator {

    public void validate(List<TransactionEntry> entries) {
        double debitSum = entries.stream()
                .filter(e -> e.getEntryType() == TransactionEntry.EntryType.Debit)
                .mapToDouble(TransactionEntry::getAmount).sum();

        double creditSum = entries.stream()
                .filter(e -> e.getEntryType() == TransactionEntry.EntryType.Credit)
                .mapToDouble(TransactionEntry::getAmount).sum();

        if (Double.compare(debitSum, creditSum) != 0.0) {
            throw new IllegalArgumentException("Debits and credits must be equal.");
        }
    }
}
