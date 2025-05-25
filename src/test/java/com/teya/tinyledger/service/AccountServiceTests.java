package com.teya.tinyledger.service;

import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.Account.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

public class AccountServiceTests {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void createAccount_shouldGenerateUniqueIdAndStoreAccount() {
        // When
        Account account = accountService.createAccount("Cash", AccountType.Asset);

        // Then
        assertThat(account).isNotNull();
        assertThat(account.getId()).isNotNull();
        assertThat(account.getName()).isEqualTo("Cash");
        assertThat(account.getType()).isEqualTo(AccountType.Asset);

        // Ensure the account is retrievable
        Account stored = accountService.getAccount(account.getId());
        assertThat(stored).isEqualTo(account);
    }

    @Test
    void getAccount_shouldReturnAccountIfExists() {
        // Given
        Account created = accountService.createAccount("Receivables", AccountType.Asset);

        // When
        Account result = accountService.getAccount(created.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Receivables");
    }

    @Test
    void getAccount_shouldReturnNullIfNotExists() {
        // When
        Account result = accountService.getAccount(999);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void getAccountById_shouldThrowIfAccountNotFound() {
        // When / Then
        assertThatThrownBy(() -> accountService.getAccountById(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void getAllAccounts_shouldReturnAllCreatedAccounts() {
        // Given
        accountService.createAccount("Bank", AccountType.Asset);
        accountService.createAccount("Credit Card", AccountType.Liability);

        // When
        Collection<Account> accounts = accountService.getAllAccounts();

        // Then
        assertThat(accounts).hasSize(2);
    }

    @Test
    void updateAccount_shouldReplaceExistingAccount() {
        // Given
        Account original = accountService.createAccount("Savings", AccountType.Asset);

        Account updated = new Account(original.getId(), "Updated Name", AccountType.Asset);
        updated.setBalance(500.0);

        // When
        accountService.updateAccount(original.getId(), updated);

        // Then
        Account result = accountService.getAccount(original.getId());
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getBalance()).isEqualTo(500.0);
    }
}

