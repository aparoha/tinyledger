package com.teya.tinyledger.service;

import com.teya.tinyledger.gen.server.model.TransactionDto;
import com.teya.tinyledger.gen.server.model.TransactionHistoryDto;
import com.teya.tinyledger.gen.server.model.TransactionRequestDto;
import com.teya.tinyledger.mapper.TransactionMapper;
import com.teya.tinyledger.model.Transaction;
import com.teya.tinyledger.model.TransactionEntry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionFacade {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final TransactionMapper transactionMapper;

    public TransactionFacade(TransactionService transactionService,
                             AccountService accountService,
                             TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.transactionMapper = transactionMapper;
    }

    public TransactionDto createTransaction(TransactionRequestDto transactionRequestDto) {
        // Convert DTO to model and prepare transaction entries
        List<TransactionEntry> entries = transactionRequestDto.getEntries().stream()
                .map(dto -> {
                    TransactionEntry entry = new TransactionEntry();
                    entry.setEntryType(TransactionEntry.EntryType.valueOf(dto.getEntryType().toString()));
                    entry.setAmount(dto.getAmount());
                    entry.setAccount(accountService.getAccount(dto.getAccount().getId()));
                    return entry;
                })
                .collect(Collectors.toList());

        // Call the TransactionService to create the transaction
        Transaction transaction = transactionService.createTransaction(
                transactionRequestDto.getDescription(),
                entries
        );

        // Return the DTO after mapping from the Transaction model
        return transactionMapper.toDto(transaction);
    }

    public List<TransactionHistoryDto> getAllTransactionHistory() {
        return transactionService.getAllTransactions().stream()
                .map(transactionMapper::toHistoryDto)
                .collect(Collectors.toList());
    }
}
