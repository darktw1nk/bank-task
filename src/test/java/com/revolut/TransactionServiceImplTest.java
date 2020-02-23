package com.revolut;

import com.revolut.database.entity.Transaction;
import com.revolut.database.repository.transaction.TransactionRepository;
import com.revolut.database.repository.transaction.TransactionRepository;
import com.revolut.database.repository.transaction.TransactionRepositoryPanacheImpl;
import com.revolut.service.transaction.TransactionProcessor;
import com.revolut.service.transaction.TransactionServiceImpl;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
we need to inject dumb objects due to to https://github.com/quarkusio/quarkus/issues/1724
without them we are getting error and can not use mockito mocking;
 */
@QuarkusTest
public class TransactionServiceImplTest {

    @Inject
    TransactionRepository dumbTransactionRepository;
    @Inject
    TransactionServiceImpl dumbTransactionService;
    @Inject
    TransactionProcessor dumbTransactionProcessor;

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TransactionProcessor transactionProcessor;
    @InjectMocks
    TransactionServiceImpl transactionService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllReturnList() {
        Mockito.doReturn(new ArrayList<>()).when(transactionRepository).getAll();

        List<Transaction> transactions = transactionService.findAll();

        Mockito.verify(transactionRepository, Mockito.only()).getAll();
        Assertions.assertNotNull(transactions);
    }

    @Test
    public void getByIdReturnOptionalWithTransaction() {
        Transaction returnTransaction = new Transaction();
        returnTransaction.setId(1L);
        returnTransaction.setAmount(new BigDecimal("100.00"));

        Mockito.doReturn(returnTransaction).when(transactionRepository).getById(1L);

        Optional<Transaction> transaction = transactionService.getById(1L);

        Assertions.assertTrue(transaction.isPresent());
        Assertions.assertEquals(0, transaction.get().getAmount().compareTo(new BigDecimal("100.00")));
    }

    @Test
    public void getByIdReturnEmptyOptional() {
        Mockito.doThrow(new NoResultException()).when(transactionRepository).getById(1L);

        Optional<Transaction> transaction = transactionService.getById(1L);

        Assertions.assertTrue(transaction.isEmpty());
    }

    @Test
    public void saveReturnOptionalWithTransaction() {
        Transaction returnTransaction = new Transaction();
        returnTransaction.setId(1L);
        returnTransaction.setAmount(new BigDecimal("100.00"));

        Mockito.doReturn(returnTransaction).when(transactionProcessor).process(returnTransaction);

        Optional<Transaction> transaction = transactionService.save(returnTransaction);

        Assertions.assertTrue(transaction.isPresent());
        Assertions.assertEquals(0, transaction.get().getAmount().compareTo(new BigDecimal("100.00")));
    }

    @Test
    public void saveReturnEmptyOptional() {
        Mockito.doReturn(null).when(transactionProcessor).process(Mockito.any());

        Optional<Transaction> transaction = transactionService.save(new Transaction());

        Assertions.assertTrue(transaction.isEmpty());
    }

    @Test
    public void deleteSuccessful(){
        Mockito.doReturn(1L).when(transactionRepository).deleteById(Mockito.any());

        boolean result = transactionService.deleteById(1L);

        Assertions.assertTrue(result);
    }

    @Test
    public void deleteUnsuccessful(){
        Mockito.doReturn(0L).when(transactionRepository).deleteById(Mockito.any());

        boolean result = transactionService.deleteById(1L);

        Assertions.assertFalse(result);
    }
}
