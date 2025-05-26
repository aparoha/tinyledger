package com.teya.tinyledger.controller;

import com.teya.tinyledger.common.ApiUtils;
import com.teya.tinyledger.common.StringUtils;
import com.teya.tinyledger.gen.server.api.DefaultApiDelegate;
import com.teya.tinyledger.gen.server.model.AccountDto;
import com.teya.tinyledger.gen.server.model.TransactionDto;
import com.teya.tinyledger.gen.server.model.TransactionHistoryDto;
import com.teya.tinyledger.gen.server.model.TransactionRequestDto;
import com.teya.tinyledger.service.AccountFacade;
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

    /**
     * Creates a new account.
     * Returns 400 if name or type is null/blank.
     * Returns 201 with the created account otherwise.
     */
    @Override
    public ResponseEntity<AccountDto> createAccount(String name, String type) {
        if (StringUtils.isNullOrBlank(name) || StringUtils.isNullOrBlank(type)) {
            return ResponseEntity.badRequest().build(); // Input validation
        }
        return ResponseEntity
                .status(201)
                .body(accountFacade.createAccount(name, type));
    }

    /**
     * Retrieves all accounts.
     * Returns 200 with list of accounts, or 500 on error.
     */
    @Override
    public ResponseEntity<List<AccountDto>> getAccounts() {
        return ApiUtils.handleRequest(accountFacade::getAccounts);
    }

    /**
     * Creates a new transaction.
     * Returns 400 if input is invalid.
     * Returns 201 with created transaction.
     * Returns 500 on other errors.
     */
    @Override
    public ResponseEntity<TransactionDto> createTransaction(TransactionRequestDto transactionRequestDto) {
        return ApiUtils.handleRequest(() -> transactionFacade
                .createTransaction(transactionRequestDto), IllegalArgumentException.class, 400);
    }

    /**
     * Gets the balance for a specific account.
     * Returns 404 if account not found.
     * Returns 200 with balance or 500 on error.
     */
    @Override
    public ResponseEntity<Double> getAccountBalance(Integer accountId) {
        return ApiUtils.handleRequest(() -> accountFacade
                .getAccountBalance(accountId), IllegalArgumentException.class, 404);
    }

    /**
     * Retrieves transaction history.
     * Returns 200 with list or 500 on error.
     */
    @Override
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory() {
        return ApiUtils.handleRequest(transactionFacade::getAllTransactionHistory);
    }
}
