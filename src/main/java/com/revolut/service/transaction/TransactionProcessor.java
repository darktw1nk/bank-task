package com.revolut.service.transaction;

import com.revolut.database.entity.Transaction;

/**
 * Represent service responsible for processing system transactions
 * it should handle full transaction lifecycle
 */
public interface TransactionProcessor {

    /**
     * method process given transaction entity
     * it should handle transferring money amount
     * and saving transaction entity to database with correct transaction status
     * @param sourceTransaction detached transaction entity (can be replaced with Immutable transaction entity later)
     * @return managed transaction entity
     */
    Transaction process(Transaction sourceTransaction);
}
