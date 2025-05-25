package com.teya.tinyledger.controller;

import com.teya.tinyledger.gen.server.model.*;
import com.teya.tinyledger.service.AccountFacade;
import com.teya.tinyledger.service.TransactionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LedgerControllerTests {

    private LedgerController ledgerController;
    private AccountFacade accountFacade;
    private TransactionFacade transactionFacade;

    @BeforeEach
    void setUp() {
        accountFacade = mock(AccountFacade.class);
        transactionFacade = mock(TransactionFacade.class);
        ledgerController = new LedgerController(accountFacade, transactionFacade);
    }

    @Test
    void shouldReturnCreatedAccountWhenInputIsValid() {
        String name = "Cash";
        AccountDto.TypeEnum type = AccountDto.TypeEnum.ASSET;
        AccountDto mockAccount = new AccountDto()
                .id(1)
                .name(name)
                .type(AccountDto.TypeEnum.ASSET)
                .balance(0.0);

        when(accountFacade.createAccount(name, type.getValue())).thenReturn(mockAccount);

        ResponseEntity<AccountDto> response = ledgerController.createAccount(name, type.getValue());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(name, response.getBody().getName());
        assertEquals(type, response.getBody().getType());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() {
        ResponseEntity<AccountDto> response = ledgerController.createAccount(" ", "Asset");
        assertEquals(400, response.getStatusCode().value());
        verify(accountFacade, never()).createAccount(any(), any());
    }

    @Test
    void shouldReturnBadRequestWhenTypeIsBlank() {
        ResponseEntity<AccountDto> response = ledgerController.createAccount("Cash", " ");
        assertEquals(400, response.getStatusCode().value());
        verify(accountFacade, never()).createAccount(any(), any());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsNull() {
        ResponseEntity<AccountDto> response = ledgerController.createAccount(null, "Asset");
        assertEquals(400, response.getStatusCode().value());
        verify(accountFacade, never()).createAccount(any(), any());
    }

    @Test
    void shouldReturnBadRequestWhenTypeIsNull() {
        ResponseEntity<AccountDto> response = ledgerController.createAccount("Cash", null);
        assertEquals(400, response.getStatusCode().value());
        verify(accountFacade, never()).createAccount(any(), any());
    }

    @Test
    void shouldReturnListOfAccountsWhenAccountsExist() {
        AccountDto.TypeEnum type = AccountDto.TypeEnum.ASSET;
        AccountDto account1 = new AccountDto().id(1).name("Cash").type(type).balance(1000.0);
        AccountDto account2 = new AccountDto().id(2).name("Bank").type(type).balance(5000.0);
        List<AccountDto> mockAccounts = Arrays.asList(account1, account2);

        when(accountFacade.getAccounts()).thenReturn(mockAccounts);

        ResponseEntity<List<AccountDto>> response = ledgerController.getAccounts();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Cash", response.getBody().get(0).getName());
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() {
        when(accountFacade.getAccounts()).thenThrow(new RuntimeException("Database error"));
        ResponseEntity<List<AccountDto>> response = ledgerController.getAccounts();
        assertEquals(500, response.getStatusCode().value());
        assertNull(response.getBody());  // No body expected on failure
    }

    @Test
    void shouldReturnEmptyListWhenNoAccountsExist() {
        when(accountFacade.getAccounts()).thenReturn(Arrays.asList());
        ResponseEntity<List<AccountDto>> response = ledgerController.getAccounts();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldReturnCreatedTransactionWhenInputIsValid() {
        
        TransactionRequestDto requestDto = new TransactionRequestDto()
                .description("Sample Transaction")
                .entries(Collections.singletonList(new TransactionRequestEntriesInnerDto()));

        TransactionDto responseDto = new TransactionDto()
                .id(new AtomicInteger(1).getAndIncrement())
                .description("Sample Transaction")
                .entries(requestDto.getEntries());

        when(transactionFacade.createTransaction(requestDto)).thenReturn(responseDto);

        // Act
        ResponseEntity<TransactionDto> response = ledgerController.createTransaction(requestDto);

        // Assert
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Sample Transaction", response.getBody().getDescription());
        verify(transactionFacade).createTransaction(requestDto);
    }

    @Test
    void shouldReturnBadRequestWhenIllegalArgumentExceptionThrown() {
        
        TransactionRequestDto requestDto = new TransactionRequestDto()
                .description("Invalid Transaction");

        when(transactionFacade.createTransaction(requestDto))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        // Act
        ResponseEntity<TransactionDto> response = ledgerController.createTransaction(requestDto);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnhandledExceptionThrown() {
        
        TransactionRequestDto requestDto = new TransactionRequestDto()
                .description("Error Transaction");

        when(transactionFacade.createTransaction(requestDto))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<TransactionDto> response = ledgerController.createTransaction(requestDto);

        assertEquals(500, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void shouldReturnOkWithBalanceWhenAccountExists() {
        
        Integer accountId = 1;
        double expectedBalance = 2500.75;
        when(accountFacade.getAccountBalance(accountId)).thenReturn(expectedBalance);

        // Act
        ResponseEntity<Double> response = ledgerController.getAccountBalance(accountId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(expectedBalance, response.getBody());
        verify(accountFacade).getAccountBalance(accountId);
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() {
        
        Integer accountId = 999;
        when(accountFacade.getAccountBalance(accountId))
                .thenThrow(new IllegalArgumentException("Account not found"));

        // Act
        ResponseEntity<Double> response = ledgerController.getAccountBalance(accountId);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(accountFacade).getAccountBalance(accountId);
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnhandledExceptionOccurs() {
        
        Integer accountId = 2;
        when(accountFacade.getAccountBalance(accountId))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Double> response = ledgerController.getAccountBalance(accountId);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(accountFacade).getAccountBalance(accountId);
    }

    @Test
    void shouldReturnTransactionHistoryWhenAvailable() {
        
        TransactionHistoryDto tx1 = new TransactionHistoryDto()
                .id(new AtomicInteger(5).getAndIncrement())
                .description("Transfer to savings")
                .date(OffsetDateTime.now());

        TransactionHistoryDto tx2 = new TransactionHistoryDto()
                .id(new AtomicInteger(10).getAndIncrement())
                .description("Loan repayment")
                .date(OffsetDateTime.now());

        List<TransactionHistoryDto> historyList = Arrays.asList(tx1, tx2);

        when(transactionFacade.getAllTransactionHistory()).thenReturn(historyList);

        // Act
        ResponseEntity<List<TransactionHistoryDto>> response = ledgerController.getTransactionHistory();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(transactionFacade).getAllTransactionHistory();
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() {
        
        when(transactionFacade.getAllTransactionHistory()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<TransactionHistoryDto>> response = ledgerController.getTransactionHistory();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(transactionFacade).getAllTransactionHistory();
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccursWithGetTransactionHistory() {
        
        when(transactionFacade.getAllTransactionHistory()).thenThrow(new RuntimeException("DB error"));

        // Act
        ResponseEntity<List<TransactionHistoryDto>> response = ledgerController.getTransactionHistory();

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(transactionFacade).getAllTransactionHistory();
    }
}