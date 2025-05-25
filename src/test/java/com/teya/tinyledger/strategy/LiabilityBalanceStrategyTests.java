package com.teya.tinyledger.strategy;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.TransactionEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class LiabilityBalanceStrategyTests {

    private LiabilityBalanceStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new LiabilityBalanceStrategy();
    }

    @Test
    void calculateNewBalance_shouldSubtractAmountForDebitEntry() {
        // Given
        Account account = new Account();
        account.setId(101);
        account.setName("Credit Card");
        account.setType(Account.AccountType.Liability);
        account.setBalance(500.0);

        TransactionEntry entry = new TransactionEntry();
        entry.setAmount(200.0);
        entry.setEntryType(TransactionEntry.EntryType.Debit);

        // When
        double newBalance = strategy.calculateNewBalance(account, entry);

        // Then
        assertThat(newBalance).isEqualTo(300.0);
    }

    @Test
    void calculateNewBalance_shouldAddAmountForCreditEntry() {
        // Given
        Account account = new Account();
        account.setId(102);
        account.setName("Loan");
        account.setType(Account.AccountType.Liability);
        account.setBalance(1000.0);

        TransactionEntry entry = new TransactionEntry();
        entry.setAmount(250.0);
        entry.setEntryType(TransactionEntry.EntryType.Credit);

        // When
        double newBalance = strategy.calculateNewBalance(account, entry);

        // Then
        assertThat(newBalance).isEqualTo(1250.0);
    }

    @Test
    void calculateNewBalance_shouldThrowExceptionWhenInsufficientBalanceForDebit() {
        // Given
        Account account = new Account();
        account.setId(103);
        account.setName("Loan");
        account.setType(Account.AccountType.Liability);
        account.setBalance(100.0);

        TransactionEntry entry = new TransactionEntry();
        entry.setAmount(150.0);
        entry.setEntryType(TransactionEntry.EntryType.Debit);

        // When / Then
        assertThatThrownBy(() -> strategy.calculateNewBalance(account, entry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient liability balance in account ID: 103");
    }
}

