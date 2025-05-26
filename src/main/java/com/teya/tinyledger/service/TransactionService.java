package com.teya.tinyledger.service;

import com.teya.tinyledger.rules.TransactionValidator;
import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.Transaction;
import com.teya.tinyledger.model.TransactionEntry;
import com.teya.tinyledger.repository.TransactionRepository;
import com.teya.tinyledger.strategy.BalanceStrategy;
import com.teya.tinyledger.strategy.BalanceStrategyFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service responsible for handling transaction-related operations.
 * This includes creating transactions, validating them, applying them to accounts,
 * and retrieving transaction history.
 */
@Service
public class TransactionService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final AtomicInteger entryIdGenerator;
    private final TransactionValidator validator;

    public TransactionService(AccountService accountService,
                              TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.entryIdGenerator = new AtomicInteger(1);
        this.validator = new TransactionValidator();
    }

    /**
     * Creates and saves a new transaction after validation and applying entries to accounts.
     *
     * @param description Description of the transaction.
     * @param entries     List of debit/credit entries in the transaction.
     * @return The persisted Transaction object.
     * @throws IllegalArgumentException if validation fails or account is invalid.
     */
    public Transaction createTransaction(String description, List<TransactionEntry> entries) {
        validator.validate(entries);
        applyEntries(entries);

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setDate(OffsetDateTime.now(ZoneOffset.UTC));
        transaction.setEntries(entries);

        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves all transactions from the repository.
     *
     * @return A collection of all transactions.
     */
    public Collection<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Applies a list of transaction entries to their respective accounts.
     * Updates account balances based on entry type and strategy.
     *
     * @param entries List of transaction entries to apply.
     * @throws IllegalArgumentException if any account is invalid.
     */
    private void applyEntries(List<TransactionEntry> entries) {
        for (TransactionEntry entry : entries) {
            Account account = accountService.getAccount(entry.getAccount().getId());
            if (account == null) {
                throw new IllegalArgumentException("Invalid account ID: " + entry.getAccount().getId());
            }

            BalanceStrategy strategy = BalanceStrategyFactory.getStrategy(account.getType());
            double newBalance = strategy.calculateNewBalance(account, entry);

            account.setBalance(newBalance);
            accountService.updateAccount(account.getId(), account);

            entry.setId(entryIdGenerator.getAndIncrement());
            entry.setAccount(account);
        }
    }
}
