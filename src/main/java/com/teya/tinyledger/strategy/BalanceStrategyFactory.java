package com.teya.tinyledger.strategy;

import com.teya.tinyledger.model.Account;

public class BalanceStrategyFactory {

    public static BalanceStrategy getStrategy(Account.AccountType type) {
        return switch (type) {
            case Asset -> new AssetBalanceStrategy();
            case Liability -> new LiabilityBalanceStrategy();
            default -> throw new IllegalArgumentException("Unsupported account type: " + type);
        };
    }
}
