package com.teya.tinyledger.repository;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.Transaction;
import com.teya.tinyledger.model.TransactionEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryTransactionRepositoryTests {

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void save_shouldAssignIdAndStoreTransaction() {
        Transaction transaction = createTransaction(null);
        Transaction saved = repository.save(transaction);
        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findAll()).containsExactly(saved);
    }

    @Test
    void save_shouldUpdateExistingTransaction() {
        Transaction transaction = createTransaction(null);
        Transaction saved = repository.save(transaction);
        saved.setDescription("Updated Description");
        Transaction updated = repository.save(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().iterator().next().getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void findAll_shouldReturnAllSavedTransactions() {
        Transaction t1 = repository.save(createTransaction(null));
        Transaction t2 = repository.save(createTransaction(null));

        Collection<Transaction> all = repository.findAll();

        assertThat(all).hasSize(2).contains(t1, t2);
    }

    private Transaction createTransaction(Integer id) {
        Account account = new Account();
        account.setId(1);
        account.setName("Bank Account");
        account.setType(Account.AccountType.Asset);
        account.setBalance(1000.0);  // assuming balance is a double

        TransactionEntry entry = new TransactionEntry();
        entry.setId(1);
        entry.setAccount(account);
        entry.setAmount(100.0);  // double type
        entry.setEntryType(TransactionEntry.EntryType.Debit);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setDescription("Test Transaction");
        transaction.setDate(OffsetDateTime.now());
        transaction.setEntries(List.of(entry));

        return transaction;
    }
}
