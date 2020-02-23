package com.revolut.database.repository.transaction;

import com.revolut.database.entity.Transaction;
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
 * Implementation of TransactionRepository
 * @see TransactionRepository
 */
@ApplicationScoped
public class TransactionRepositoryPanacheImpl implements PanacheRepository<Transaction>,TransactionRepository {

    /**
     * This method is for retrieving all transactions persisted in database
     * not pageable, use with caution
     * @return list of all found transactions
     */
    @Override
    public List<Transaction> getAll() {
        return listAll();
    }

    /**
     *  retrieves information about transaction with given id
     * @param id transaction id
     * @return transaction entity, if found or
     * @throws NoResultException if entity was not found
     */
    @Override
    public Transaction getById(Long id) throws NoResultException {
        return find("id=:id", Parameters.with("id", id)).singleResult();
    }

    /**
     *  retrieves transaction with given id and
     *  set pessimistic write lock on it
     * @param id transaction id
     * @return entity if found, null if not found
     */
    @Override
    public Transaction getByIdForUpdate(Long id) {
        return findById(id, LockModeType.PESSIMISTIC_WRITE);
    }

    /**
     * saves given transaction entity
     * this method contains simple logic to detect is this entity
     * need to be merged of persisted
     * checks if entity
     * @param entity entity
     * @return managed transaction entity
     */
    @Transactional
    @Override
    public Transaction save(Transaction entity) {
        EntityManager em = JpaOperations.getEntityManager();
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);

        }
    }

    /**
     * tries to delete transaction with given id
     * @param id account id
     * @return number of accounts being affected
     */
    @Transactional
    @Override
    public long deleteById(Long id) {
        return delete("id=?1", id);
    }
}
