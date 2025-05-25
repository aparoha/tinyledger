package com.teya.tinyledger.strategy;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.TransactionEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class AssetBalanceStrategyTests {

    private AssetBalanceStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AssetBalanceStrategy();
    }

    @Test
    void calculateNewBalance_shouldAddAmountForDebitEntry() {
        // Given
        Account account = new Account();
        account.setId(1);
        account.setName("Cash");
        account.setType(Account.AccountType.Asset);
        account.setBalance(1000.0);

        TransactionEntry entry = new TransactionEntry();
        entry.setAmount(200.0);
        entry.setEntryType(TransactionEntry.EntryType.Debit);

        // When
        double newBalance = strategy.calculateNewBalance(account, entry);

        // Then
        assertThat(newBalance).isEqualTo(1200.0);
    }

    @Test
    void calculateNewBalance_shouldSubtractAmountForCreditEntry() {
        // Given
        Account account = new Account();
        account.setId(2);
        account.setName("Cash");
        account.setType(Account.AccountType.Asset);
        account.setBalance(500.0);

        TransactionEntry entry = new TransactionEntry();
        entry.setAmount(150.0);
        entry.setEntryType(TransactionEntry.EntryType.Credit);

        // When
        double newBalance = strategy.calculateNewBalance(account, entry);

        // Then
        assertThat(newBalance).isEqualTo(350.0);
    }

    @Test
    void calculateNewBalance_shouldThrowExceptionWhenInsufficientBalanceForCredit() {
        // Given
        Account account = new Account();
        account.setId(3);
        account.setName("Cash");
        account.setType(Account.AccountType.Asset);
        account.setBalance(100.0);

        TransactionEntry entry = new TransactionEntry();
        entry.setAmount(150.0);
        entry.setEntryType(TransactionEntry.EntryType.Credit);

        // When / Then
        assertThatThrownBy(() -> strategy.calculateNewBalance(account, entry))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient asset balance in account ID: 3");
    }
}

