package com.teya.tinyledger.controller;

import com.teya.tinyledger.gen.server.model.AccountDto;
import com.teya.tinyledger.gen.server.model.TransactionDto;
import com.teya.tinyledger.gen.server.model.TransactionHistoryDto;
import com.teya.tinyledger.gen.server.model.TransactionRequestDto;
import com.teya.tinyledger.service.AccountFacade;
import com.teya.tinyledger.gen.server.api.DefaultApiDelegate;
import com.teya.tinyledger.service.TransactionFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LedgerController implements DefaultApiDelegate {
    private final AccountFacade accountFacade;
    private final TransactionFacade transactionFacade;

    public LedgerController(AccountFacade accountFacade, TransactionFacade transactionFacade) {
        this.accountFacade = accountFacade;
        this.transactionFacade = transactionFacade;
    }
    @Override
    public ResponseEntity<AccountDto> createAccount(String name, String type) {
        if (name == null || type == null || name.isBlank() || type.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity
                .status(201)
                .body(accountFacade.createAccount(name, type));
    }

    @Override
    public ResponseEntity<List<AccountDto>> getAccounts() {
        try {
            return ResponseEntity.ok(accountFacade.getAccounts());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<TransactionDto> createTransaction(TransactionRequestDto transactionRequestDto) {
        try {
            TransactionDto transactionDto = transactionFacade.createTransaction(transactionRequestDto);
            return ResponseEntity.status(201).body(transactionDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<Double> getAccountBalance(Integer accountId) {
        try {
            double balance = accountFacade.getAccountBalance(accountId);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build(); // Return 404 if account is not found
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Return 500 if there are other errors
        }
    }

    @Override
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory() {
        try {
            List<TransactionHistoryDto> history = transactionFacade.getAllTransactionHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
