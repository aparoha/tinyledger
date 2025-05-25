package com.teya.tinyledger.mapper;

import com.teya.tinyledger.gen.server.model.AccountDto;
import com.teya.tinyledger.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountDto toCreateAccountDto(Account account) {
        return new AccountDto()
                .id(account.getId())
                .name(account.getName())
                .type(AccountDto.TypeEnum.valueOf(account.getType().toString().toUpperCase()))
                .balance(account.getBalance());
    }
}
