package com.teya.tinyledger.rules;

import com.teya.tinyledger.model.TransactionEntry;

import java.util.List;

/**
 * Validates a list of transaction entries to ensure accounting integrity.
 * Specifically, it ensures that the sum of debit entries equals the sum of credit entries,
 * adhering to the double-entry accounting principle.
 */
public class TransactionValidator {

    /**
     * Validates that the total debits equal the total credits.
     *
     * @param entries A list of transaction entries to validate.
     * @throws IllegalArgumentException if debits and credits are not balanced.
     */
    public void validate(List<TransactionEntry> entries) {
        // Sum all debit entry amounts
        double debitSum = entries.stream()
                .filter(e -> e.getEntryType() == TransactionEntry.EntryType.Debit)
                .mapToDouble(TransactionEntry::getAmount).sum();

        // Sum all credit entry amounts
        double creditSum = entries.stream()
                .filter(e -> e.getEntryType() == TransactionEntry.EntryType.Credit)
                .mapToDouble(TransactionEntry::getAmount).sum();

        // Check if the total debits and credits are equal
        if (Double.compare(debitSum, creditSum) != 0.0) {
            throw new IllegalArgumentException("Debits and credits must be equal.");
        }
    }
}
