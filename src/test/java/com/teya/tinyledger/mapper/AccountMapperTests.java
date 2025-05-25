package com.teya.tinyledger.mapper;

import com.teya.tinyledger.gen.server.model.AccountDto;
import com.teya.tinyledger.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountMapperTests {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
    }

    @Test
    void testToCreateAccountDto_shouldMapFieldsCorrectly() {
        Account account = new Account();
        account.setId(123);
        account.setName("Cash Account");
        account.setType(Account.AccountType.Asset);
        account.setBalance(1000.00);

        AccountDto dto = accountMapper.toCreateAccountDto(account);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(123);
        assertThat(dto.getName()).isEqualTo("Cash Account");
        assertThat(dto.getType()).isEqualTo(AccountDto.TypeEnum.ASSET);
        assertThat(dto.getBalance()).isEqualByComparingTo(1000.00);
    }

    @Test
    void testToCreateAccountDto_withDifferentAccountType() {
        Account account = new Account();
        account.setId(456);
        account.setName("Credit Card");
        account.setType(Account.AccountType.Liability);
        account.setBalance(500.00);

        AccountDto dto = accountMapper.toCreateAccountDto(account);

        assertThat(dto.getType()).isEqualTo(AccountDto.TypeEnum.LIABILITY);
    }
}
