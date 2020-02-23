package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.database.entity.Transaction;
import com.revolut.resource.AccountResource;
import com.revolut.resource.TransactionResource;
import com.revolut.service.account.AccountService;
import com.revolut.service.transaction.TransactionService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

/*
we need to inject dumb objects due to to https://github.com/quarkusio/quarkus/issues/1724
without them we are getting error and can not use mockito mocking;
 */
@QuarkusTest
public class TransactionResourceTest {

    @Inject
    TransactionService dumbTransactionService;
    @Inject
    TransactionResource dumbTransactionResource;


    @Mock
    UriInfo uriInfo;
    @Mock
    TransactionService transactionService;
    @InjectMocks
    TransactionResource transactionResource;

    @BeforeEach
    public void init() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
        UriBuilder uriBuilder = Mockito.mock(UriBuilder.class);
        URI uri = new URI("localhost");
        Mockito.doReturn(uriBuilder).when(uriInfo).getBaseUriBuilder();
        Mockito.doReturn(uriBuilder).when(uriBuilder).path(Mockito.anyString());
        Mockito.doReturn(uri).when(uriBuilder).build(Mockito.any());
    }

    @Test
    public void getAllTransactionsReturnsOkTest() {
        Mockito.doReturn(new ArrayList<>()).when(transactionService).findAll();

        Response response = transactionResource.getAllTransactions();

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getParticularTransactionReturnsOk(){
        Mockito.doReturn(Optional.of(new Transaction())).when(transactionService).getById(Mockito.any());

        Response response = transactionResource.getTransaction(1L);

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getParticularTransactionReturnsNotFound(){
        Mockito.doReturn(Optional.empty()).when(transactionService).getById(Mockito.any());

        Response response = transactionResource.getTransaction(1L);

        Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    public void createTransactionSendEmptyTransaction(){
        Response response = transactionResource.createTransaction(null);

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void createTransactionSaveSuccessful(){
        Long id = 2L;
        Transaction transaction = new Transaction();
        transaction.setId(id);
        Mockito.doReturn(Optional.of(transaction)).when(transactionService).save(Mockito.any());


        Response response = transactionResource.createTransaction(transaction);

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    public void createTransactionSaveUnsuccessful(){
        Long id = 2L;
        Transaction transaction = new Transaction();
        transaction.setId(id);
        Mockito.doReturn(Optional.empty()).when(transactionService).save(Mockito.any());

        Response response = transactionResource.createTransaction(transaction);
        Assertions.assertEquals(400, response.getStatus());
    }
    
}
