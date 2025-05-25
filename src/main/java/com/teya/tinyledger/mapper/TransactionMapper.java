package com.teya.tinyledger.mapper;

import com.teya.tinyledger.gen.server.model.*;
import com.teya.tinyledger.model.Transaction;
import com.teya.tinyledger.model.TransactionEntry;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        var transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setDescription(transaction.getDescription());
        transactionDto.setDate(transaction.getDate());

        List<TransactionRequestEntriesInnerDto> entries = transaction.getEntries().stream().map(entry -> {
            var entryDto = new TransactionRequestEntriesInnerDto();
            entryDto.setAmount(entry.getAmount());

            var accountDto = new AccountDto();
            accountDto.setId(entry.getAccount().getId());
            accountDto.setName(entry.getAccount().getName());
            accountDto.setType(AccountDto.TypeEnum.valueOf(entry.getAccount().getType().toString().toUpperCase()));
            accountDto.setBalance(entry.getAccount().getBalance());

            entryDto.setAccount(accountDto);
            entryDto.setEntryType(entry.getEntryType() == TransactionEntry.EntryType.Debit
                    ? TransactionRequestEntriesInnerDto.EntryTypeEnum.DEBIT
                    : TransactionRequestEntriesInnerDto.EntryTypeEnum.CREDIT);
            return entryDto;
        }).collect(Collectors.toList());

        transactionDto.setEntries(entries);
        return transactionDto;
    }

    public TransactionHistoryDto toHistoryDto(Transaction transaction) {
        TransactionHistoryDto dto = new TransactionHistoryDto();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setDate(transaction.getDate());
        dto.setEntries(
                transaction.getEntries().stream().map(entry -> {
                    TransactionEntryDto entryDto = new TransactionEntryDto();
                    entryDto.setId(entry.getId());
                    entryDto.setAmount(entry.getAmount());
                    entryDto.setEntryType(entry.getEntryType() == TransactionEntry.EntryType.Debit
                            ? TransactionEntryDto.EntryTypeEnum.DEBIT
                            : TransactionEntryDto.EntryTypeEnum.CREDIT);
                    AccountDto accountDto = new AccountDto();
                    accountDto.setId(entry.getAccount().getId());
                    accountDto.setName(entry.getAccount().getName());
                    accountDto.setBalance(entry.getAccount().getBalance());
                    accountDto.setType(AccountDto.TypeEnum.valueOf(entry.getAccount().getType().toString().toUpperCase()));
                    entryDto.setAccount(accountDto);
                    return entryDto;
                }).collect(Collectors.toList())
        );
        return dto;
    }
}
