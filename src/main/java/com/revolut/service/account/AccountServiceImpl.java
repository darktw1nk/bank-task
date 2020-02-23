package com.revolut.service.account;

import com.revolut.database.entity.Account;
import com.revolut.database.repository.account.AccountRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AccountService
 * @see AccountService
 */
@ApplicationScoped
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getName());

    @Inject
    AccountRepository accountRepository;

    /**
     * This method is for retrieving all accounts in our system
     * not pageable, use with caution
     * @return list of all found accounts
     */
    @Override
    public List<Account> findAll() {
        return accountRepository.getAll();
    }

    /**
     * Safely for NullPointerException retrieves information about account with given id
     * @param id account id
     * @return an {@link Optional} describing Account
     */
    @Override
    public Optional<Account> getById(Long id) {
        Account account = null;
        try {
            account = accountRepository.getById(id);
        } catch (NoResultException e) {
            logger.error("",e);
        }
        return Optional.ofNullable(account);
    }

    /**
     * Safely for NullPointerException creates or updates Account entity
     * @param account entity to save or update
     * @return an {@link Optional} describing Account
     */
    @Override
    public Optional<Account> save(Account account) {
        return Optional.ofNullable(accountRepository.save(account));
    }

    /**
     * Tries to delete account with given id
     * @param id of account to be deleted
     * @return boolean indicating if account was deleted
     */
    @Override
    public boolean deleteById(Long id) {
        return accountRepository.deleteById(id) > 0;
    }
}
