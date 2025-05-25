package com.teya.tinyledger.service;

import com.teya.tinyledger.model.Account;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService {

    private ConcurrentHashMap<Integer, Account> accountMap;
    private AtomicInteger accountIdGenerator;

    public AccountService() {
        this.accountMap = new ConcurrentHashMap<>();
        this.accountIdGenerator = new AtomicInteger(1);
    }

    public Account createAccount(String name, Account.AccountType type) {
        int id = accountIdGenerator.getAndIncrement();
        Account account = new Account(id, name, type);
        accountMap.put(id, account);
        return account;
    }

    public Account getAccount(int id) {
        return accountMap.get(id);
    }

    public Collection<Account> getAllAccounts() {

        return accountMap.values();
    }

    public Account getAccountById(int accountId) {
        Account account = accountMap.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return account;
    }

    public void updateAccount(Integer id, Account account) {
        accountMap.put(id, account);
    }
}
