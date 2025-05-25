package com.teya.tinyledger.rules;

import com.teya.tinyledger.model.TransactionEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static com.teya.tinyledger.model.TransactionEntry.EntryType.Credit;
import static com.teya.tinyledger.model.TransactionEntry.EntryType.Debit;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionValidatorTests {

    private TransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }

    @Test
    void shouldPassValidationWhenDebitsEqualCredits() {
        TransactionEntry debit = new TransactionEntry();
        debit.setEntryType(Debit);
        debit.setAmount(100.0);

        TransactionEntry credit = new TransactionEntry();
        credit.setEntryType(Credit);
        credit.setAmount(100.0);

        List<TransactionEntry> entries = Arrays.asList(debit, credit);

        assertDoesNotThrow(() -> validator.validate(entries));
    }

    @Test
    void shouldThrowExceptionWhenDebitsDoNotEqualCredits() {
        TransactionEntry debit = new TransactionEntry();
        debit.setEntryType(Debit);
        debit.setAmount(150.0);

        TransactionEntry credit = new TransactionEntry();
        credit.setEntryType(Credit);
        credit.setAmount(100.0);

        List<TransactionEntry> entries = Arrays.asList(debit, credit);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validator.validate(entries));

        assertEquals("Debits and credits must be equal.", exception.getMessage());
    }

    @Test
    void shouldPassValidationWithMultipleBalancedEntries() {
        TransactionEntry debit1 = new TransactionEntry();
        debit1.setEntryType(Debit);
        debit1.setAmount(60.0);

        TransactionEntry debit2 = new TransactionEntry();
        debit2.setEntryType(Debit);
        debit2.setAmount(40.0);

        TransactionEntry credit = new TransactionEntry();
        credit.setEntryType(Credit);
        credit.setAmount(100.0);

        List<TransactionEntry> entries = Arrays.asList(debit1, debit2, credit);

        assertDoesNotThrow(() -> validator.validate(entries));
    }
}
