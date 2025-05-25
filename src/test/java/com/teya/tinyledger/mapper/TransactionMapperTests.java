package com.teya.tinyledger.mapper;

import com.teya.tinyledger.gen.server.model.AccountDto;
import com.teya.tinyledger.gen.server.model.TransactionDto;
import com.teya.tinyledger.gen.server.model.TransactionRequestEntriesInnerDto;
import com.teya.tinyledger.model.Account;
import com.teya.tinyledger.model.Transaction;
import com.teya.tinyledger.model.TransactionEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapper();
    }

    @Test
    void shouldMapTransactionToTransactionDtoCorrectly() {
        Transaction transaction = new Transaction();
        OffsetDateTime testDate = OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
        transaction.setId(123);
        transaction.setDescription("Payment to vendor");
        transaction.setDate(testDate);

        Account account = new Account();
        account.setId(1);
        account.setName("Cash");
        account.setBalance(1000.00);
        account.setType(Account.AccountType.Asset);

        TransactionEntry entry = new TransactionEntry();
        entry.setId(1);
        entry.setAmount(200.00);
        entry.setAccount(account);
        entry.setEntryType(TransactionEntry.EntryType.Debit);

        transaction.setEntries(List.of(entry));

        TransactionDto dto = transactionMapper.toDto(transaction);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(123);
        assertThat(dto.getDescription()).isEqualTo("Payment to vendor");
        assertThat(dto.getDate()).isEqualTo(testDate);

        assertThat(dto.getEntries()).hasSize(1);
        TransactionRequestEntriesInnerDto entryDto = dto.getEntries().get(0);
        assertThat(entryDto.getAmount()).isEqualByComparingTo(200.00);
        assertThat(entryDto.getEntryType()).isEqualTo(TransactionRequestEntriesInnerDto.EntryTypeEnum.DEBIT);

        AccountDto accountDto = entryDto.getAccount();
        assertThat(accountDto).isNotNull();
        assertThat(accountDto.getId()).isEqualTo(1);
        assertThat(accountDto.getName()).isEqualTo("Cash");
        assertThat(accountDto.getType()).isEqualTo(AccountDto.TypeEnum.ASSET);
        assertThat(accountDto.getBalance()).isEqualByComparingTo(1000.00);
    }
}

