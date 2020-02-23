package com.revolut.database.repository.account;

import com.revolut.database.entity.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Implementation of AccountRepository
 * @see AccountRepository
 */
@ApplicationScoped
public class AccountRepositoryPanacheImpl implements PanacheRepository<Account>, AccountRepository {

    /**
     * This method is for retrieving all account persisted in database
     * not pageable, use with caution
     * @return list of all found accounts
     */
    @Override
    public List<Account> getAll() {
        return listAll();
    }

    /**
     *  retrieves information about account with given id
     * @param id account id
     * @return account entity, if found or
     * @throws NoResultException if entity was not found
     */
    @Override
    public Account getById(Long id) throws NoResultException {
        return find("id=:id", Parameters.with("id", id)).singleResult();
    }

    /**
     *  retrieves account with given id and
     *  set pessimistic write lock on it
     * @param id account id
     * @return entity if found, null if not found
     */
    @Override
    public Account getByIdForUpdate(Long id) {
        return findById(id, LockModeType.PESSIMISTIC_WRITE);
    }

    /**
     * saves given account entity
     * this method contains simple logic to detect is this entity
     * need to be merged of persisted
     * checks if entity
     * @param account entity
     * @return managed account entity
     */
    @Transactional
    @Override
    public Account save(Account account) {
        EntityManager em = JpaOperations.getEntityManager();
        if (account.getId() == null) {
            em.persist(account);
            return account;
        } else {
            return em.merge(account);

        }
    }

    /**
     * tries to delete account with given id
     * @param id account id
     * @return number of accounts being affected
     */
    @Transactional
    @Override
    public long deleteById(Long id) {
        return delete("id=?1", id);
    }

}
