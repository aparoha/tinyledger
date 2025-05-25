package com.teya.tinyledger.service;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.Transaction;
import com.teya.tinyledger.model.TransactionEntry;
import com.teya.tinyledger.repository.TransactionRepository;
import com.teya.tinyledger.strategy.BalanceStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Collection;
import java.util.List;
import static com.teya.tinyledger.model.TransactionEntry.EntryType.Credit;
import static com.teya.tinyledger.model.TransactionEntry.EntryType.Debit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TransactionServiceTests {

    private AccountService accountService;
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(accountService, transactionRepository);
    }

    @Test
    void createTransaction_shouldApplyEntriesAndSaveTransaction() {
        // Given
        Account account = new Account();
        account.setId(1);
        account.setType(Account.AccountType.Asset);
        account.setBalance(1000.0);

        TransactionEntry entry1 = new TransactionEntry();
        entry1.setAccount(account);
        entry1.setAmount(100.0);
        entry1.setEntryType(TransactionEntry.EntryType.Debit);

        TransactionEntry entry2 = new TransactionEntry();
        entry2.setAccount(account);
        entry2.setAmount(100.0);
        entry2.setEntryType(TransactionEntry.EntryType.Credit);

        when(accountService.getAccount(1)).thenReturn(account);

        BalanceStrategy strategy = mock(BalanceStrategy.class);
        when(strategy.calculateNewBalance(account, entry1)).thenReturn(1100.0);
        when(strategy.calculateNewBalance(account, entry2)).thenReturn(1000.0);  // added

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Transaction result = transactionService.createTransaction("Deposit", List.of(entry1, entry2));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Deposit");
        assertThat(result.getEntries()).hasSize(2);
        assertThat(result.getEntries().get(0).getId()).isNotNull();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountService, times(2)).updateAccount(eq(1), accountCaptor.capture());

        List<Account> updates = accountCaptor.getAllValues();
        assertThat(updates.get(0).getBalance()).isEqualTo(1000.0); // after debit
        assertThat(updates.get(1).getBalance()).isEqualTo(1000.0); // after credit

        verify(transactionRepository).save(any(Transaction.class));
    }


    @Test
    void createTransaction_shouldThrowExceptionForInvalidAccount() {
        // Given
        Account dummyAccount = new Account();
        dummyAccount.setId(999);  // nonexistent

        TransactionEntry entry1 = new TransactionEntry();
        entry1.setAccount(dummyAccount);
        entry1.setAmount(50.0);
        entry1.setEntryType(Credit);

        TransactionEntry entry2 = new TransactionEntry();
        entry2.setAccount(dummyAccount);
        entry2.setAmount(50.0);
        entry2.setEntryType(Debit);

        when(accountService.getAccount(999)).thenReturn(null);

        // When / Then
        assertThatThrownBy(() ->
                transactionService.createTransaction("Invalid Test", List.of(entry1, entry2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid account ID: 999");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getAllTransactions_shouldReturnAllFromRepository() {
        // Given
        Transaction t1 = new Transaction();
        t1.setDescription("T1");
        Transaction t2 = new Transaction();
        t2.setDescription("T2");

        when(transactionRepository.findAll()).thenReturn(List.of(t1, t2));

        // When
        Collection<Transaction> transactions = transactionService.getAllTransactions();

        // Then
        assertThat(transactions).hasSize(2).containsExactly(t1, t2);
    }
}
