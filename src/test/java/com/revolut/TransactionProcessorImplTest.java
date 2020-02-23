package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.database.entity.Transaction;
import com.revolut.database.entity.TransactionStatus;
import com.revolut.database.repository.account.AccountRepository;
import com.revolut.database.repository.transaction.TransactionRepository;
import com.revolut.service.transaction.TransactionProcessorImpl;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import java.math.BigDecimal;

/*
we need to inject dumb objects due to to https://github.com/quarkusio/quarkus/issues/1724
without them we are getting error and can not use mockito mocking;
 */
@QuarkusTest
public class TransactionProcessorImplTest {

    @Inject
    AccountRepository dumbAccountRepository;
    @Inject
    TransactionRepository dumbTransactionRepository;
    @Inject
    TransactionProcessorImpl dumbTransactionProcessor;

    @Mock
    AccountRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    TransactionProcessorImpl transactionProcessor;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processReturnsSuccessfulTransaction() {
        Transaction source = new Transaction();
        source.setDebit(1L);
        source.setCredit(2L);
        source.setAmount(new BigDecimal("100.00"));

        Account debitAccount = new Account();
        debitAccount.setId(1L);
        debitAccount.setName("Rick");
        debitAccount.setBalance(new BigDecimal("100.00"));

        Account creditAccount = new Account();
        creditAccount.setId(1L);
        creditAccount.setName("Morty");
        creditAccount.setBalance(new BigDecimal("100.00"));

        Mockito.doReturn(debitAccount).when(accountRepository).getByIdForUpdate(1L);
        Mockito.doReturn(creditAccount).when(accountRepository).getByIdForUpdate(2L);
        Mockito.doAnswer(i -> i.getArguments()[0]).when(transactionRepository).save(Mockito.any());

        Transaction transaction = transactionProcessor.process(source);

        Assertions.assertNotNull(transaction);
        Assertions.assertEquals(TransactionStatus.SUCCESSFUL,transaction.getStatus());
    }

    @Test
    public void processReturnsUnsuccessfulTransaction() {
        Transaction source = new Transaction();
        source.setDebit(1L);
        source.setCredit(2L);
        source.setAmount(new BigDecimal("100.00"));

        Account debitAccount = new Account();
        debitAccount.setId(1L);
        debitAccount.setName("Rick");
        debitAccount.setBalance(new BigDecimal("50.00"));

        Account creditAccount = new Account();
        creditAccount.setId(1L);
        creditAccount.setName("Morty");
        creditAccount.setBalance(new BigDecimal("50.00"));

        Mockito.doReturn(debitAccount).when(accountRepository).getByIdForUpdate(1L);
        Mockito.doReturn(creditAccount).when(accountRepository).getByIdForUpdate(2L);
        Mockito.doAnswer(i -> i.getArguments()[0]).when(transactionRepository).save(Mockito.any());

        Transaction transaction = transactionProcessor.process(source);

        Assertions.assertNotNull(transaction);
        Assertions.assertEquals(TransactionStatus.UNSUCCESSFUL,transaction.getStatus());
    }

}
