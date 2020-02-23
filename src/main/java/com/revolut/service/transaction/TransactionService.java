package com.revolut.service.transaction;

import com.revolut.database.entity.Transaction;
import com.revolut.service.CrudService;

/**
 * This class is for performing operations on Transaction entity
 * contains only methods extended from CrudService, but for Transaction entity
 * @see Transaction
 * @see CrudService
 */
public interface TransactionService extends CrudService<Transaction> {

}
