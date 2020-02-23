package com.revolut.service.transaction;

import com.revolut.database.entity.Transaction;
import com.revolut.database.repository.transaction.TransactionRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TransactionService
 * @see TransactionService
 */
@ApplicationScoped
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = Logger.getLogger(TransactionServiceImpl.class.getName());

    @Inject
    TransactionRepository transactionRepository;
    @Inject
    TransactionProcessor transactionProcessor;

    /**
     * This method is for retrieving all transactions in our system
     * not pageable, use with caution
     * @return list of all found transactions
     */
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.getAll();
    }

    /**
     * Safely for NullPointerException retrieves information about transaction with given id
     * @param id transaction id
     * @return an {@link Optional} describing Transaction
     */
    @Override
    public Optional<Transaction> getById(Long id) {
        Transaction transaction = null;
        try {
            transaction = transactionRepository.getById(id);
        } catch (NoResultException e) {
            logger.error("",e);
        }
        return Optional.ofNullable(transaction);
    }

    /**
     * Safely for NullPointerException creates or updates Transaction entity
     * @param entity entity to save or update
     * @return an {@link Optional} describing Transaction
     */
    @Override
    public Optional<Transaction> save(Transaction entity) {
        Transaction transaction = transactionProcessor.process(entity);
        return Optional.ofNullable(transaction);
    }

    /**
     * Tries to delete transaction with given id
     * @param id of transaction to be deleted
     * @return boolean indicating if transaction was deleted
     */
    @Override
    public boolean deleteById(Long id) {
        return transactionRepository.deleteById(id) > 0;
    }
}
