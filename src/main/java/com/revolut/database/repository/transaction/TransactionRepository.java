package com.revolut.database.repository.transaction;

import com.revolut.database.entity.Transaction;
import com.revolut.database.repository.CrudRepository;

/**
 * This class is for retrieving information about transactions from database
 * contains only methods extended from CrudRepository, but for Transaction entity
 * @see CrudRepository
 */
public interface TransactionRepository extends CrudRepository<Transaction> {

}
