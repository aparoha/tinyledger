package com.teya.tinyledger.model;

public class Account {
    private Integer id;
    private String name;
    private AccountType type;
    private double balance;

    public Account() {}

    public Account(Integer id, String name, AccountType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = 0.0;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public enum AccountType {
        Asset, Liability
    }
}
