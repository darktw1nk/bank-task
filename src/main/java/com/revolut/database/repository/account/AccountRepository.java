package com.revolut.database.repository.account;

import com.revolut.database.entity.Account;
import com.revolut.database.repository.CrudRepository;

/**
 * This class is for retrieving information about account from database
 * contains only methods extended from CrudRepository, but for Account entity
 * @see CrudRepository
 */
public interface AccountRepository extends CrudRepository<Account> {

}
