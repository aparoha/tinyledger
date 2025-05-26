package com.teya.tinyledger.service;

import com.teya.tinyledger.gen.server.model.AccountDto;
import com.teya.tinyledger.mapper.AccountMapper;
import com.teya.tinyledger.model.Account;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

@Service
public class AccountFacade {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountFacade(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    public AccountDto createAccount(String name, String type) {
        Account account = accountService.createAccount(name, Account.AccountType.valueOf(type));
        return accountMapper.toCreateAccountDto(account);
    }

    public List<AccountDto> getAccounts() {
        Collection<Account> accounts = accountService.getAllAccounts();
        return accounts
                .stream()
                .map(account -> accountMapper.toCreateAccountDto(account))
                .toList();
    }
    public double getAccountBalance(int accountId) {
        Account account = accountService.getAccountById(accountId);
        return account.getBalance(); // Return the balance of the account
    }
}

