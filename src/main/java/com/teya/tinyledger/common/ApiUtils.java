package com.teya.tinyledger.common;

import org.springframework.http.ResponseEntity;
import java.util.function.Supplier;

public class ApiUtils {
    /**
     * Handles successful requests or returns 500 on any error.
     */
    public static <T> ResponseEntity<T> handleRequest(Supplier<T> supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Handles requests with known and unknown exceptions.
     * Returns knownStatus for knownException types.
     * Returns 500 for others.
     */
    public static <T, E extends Exception> ResponseEntity<T> handleRequest(
            Supplier<T> supplier,
            Class<E> knownException,
            int knownStatus
    ) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (Exception e) {
            if (knownException.isInstance(e)) {
                return ResponseEntity.status(knownStatus).build();
            }
            return ResponseEntity.status(500).build();
        }
    }
}
