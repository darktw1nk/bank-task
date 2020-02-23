package com.revolut.service.transaction;

import com.revolut.database.entity.Account;
import com.revolut.database.entity.Transaction;
import com.revolut.database.entity.TransactionStatus;
import com.revolut.database.repository.account.AccountRepository;
import com.revolut.database.repository.transaction.TransactionRepository;
import com.revolut.service.transaction.exception.InsufficientFundsException;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.*;
import java.math.BigDecimal;

/**
 * Implementation of TransactionProcessor
 * @see TransactionProcessor
 */
@ApplicationScoped
public class TransactionProcessorImpl implements TransactionProcessor {
    private static final Logger logger = Logger.getLogger(TransactionProcessorImpl.class.getName());

    @Inject
    AccountRepository accountRepository;
    @Inject
    TransactionRepository transactionRepository;

    /**
     * handles processing of given transaction
     * try to transfer amount between accounts and save transaction with correct status to database
     * method is transactional cause we need to store information about all transactions
     * and should rollback if there is an exception on transaction saving step
     * @param sourceTransaction detached transaction entity (can be replaced with Immutable transaction entity later)
     * @return managed transaction entity
     */
    @Transactional
    public Transaction process(Transaction sourceTransaction) {
        Transaction transaction = new Transaction();
        transaction.setDebit(sourceTransaction.getDebit());
        transaction.setCredit(sourceTransaction.getCredit());
        transaction.setAmount(sourceTransaction.getAmount());

        try {
            transferFundsBetweenAccounts(transaction.getDebit(), transaction.getCredit(), transaction.getAmount());
            transaction.setStatus(TransactionStatus.SUCCESSFUL);
        } catch (Exception e) {
            logger.error("",e);
            transaction.setStatus(TransactionStatus.UNSUCCESSFUL);
        }

        transaction = transactionRepository.save(transaction);
        logger.debug("transferred amount: "+sourceTransaction.getAmount());
        return transaction;
    }

    /**
     * handles transfer of money amount between given account
     * handles locks of both accounts
     * checks if source account have enough funds
     * @param debitId source account id
     * @param creditId target account id
     * @param amount to be transferred
     * @throws InsufficientFundsException if source account do not have enough funds
     */
    private void transferFundsBetweenAccounts(Long debitId, Long creditId, BigDecimal amount) throws InsufficientFundsException{
        Account debit = accountRepository.getByIdForUpdate(debitId);
        Account credit = accountRepository.getByIdForUpdate(creditId);

        if (debit.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(debitId, debit.getBalance(), amount);
        }

        debit.setBalance(debit.getBalance().subtract(amount));
        credit.setBalance(credit.getBalance().add(amount));

        accountRepository.save(debit);
        accountRepository.save(credit);
    }
}
