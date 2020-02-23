package com.revolut.service.account;

import com.revolut.database.entity.Account;
import com.revolut.service.CrudService;

/**
 * This class is for performing operations on Account entity
 * contains only methods extended from CrudService, but for Account entity
 * @see Account
 * @see CrudService
 */
public interface AccountService extends CrudService<Account> {

}
