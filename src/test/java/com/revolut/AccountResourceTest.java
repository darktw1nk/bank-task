package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.resource.AccountResource;
import com.revolut.service.account.AccountService;
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
public class AccountResourceTest {

    @Inject
    AccountService dumbAccountService;
    @Inject
    AccountResource dumbAccountResource;


    @Mock
    UriInfo uriInfo;
    @Mock
    AccountService accountService;
    @InjectMocks
    AccountResource accountResource;

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
    public void getAllAccountsReturnsOkTest() {
        Mockito.doReturn(new ArrayList<>()).when(accountService).findAll();

        Response response = accountResource.getAllAccounts();

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getParticularAccountReturnsOk(){
        Mockito.doReturn(Optional.of(new Account())).when(accountService).getById(Mockito.any());

        Response response = accountResource.getAccount(1L);

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void getParticularAccountReturnsNotFound(){
        Mockito.doReturn(Optional.empty()).when(accountService).getById(Mockito.any());

        Response response = accountResource.getAccount(1L);

        Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    public void createAccountSendEmptyAccount(){
        Response response = accountResource.createAccount(null);

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void createAccountSaveSuccessful(){
        Long id = 2L;
        Account account = new Account();
        account.setId(id);
        Mockito.doReturn(Optional.of(account)).when(accountService).save(Mockito.any());


        Response response = accountResource.createAccount(account);

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    public void createAccountSaveUnsuccessful(){
        Long id = 2L;
        Account account = new Account();
        account.setId(id);
        Mockito.doReturn(Optional.empty()).when(accountService).save(Mockito.any());

        Response response = accountResource.createAccount(account);
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void updateAccountSendEmpty(){
        Response response = accountResource.updateAccount(1L,null);

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void updateAccountExisted(){
        Account account = new Account();
        account.setId(1L);

        Mockito.doReturn(Optional.of(account)).when(accountService).getById(1L);
        Mockito.doReturn(Optional.of(new Account())).when(accountService).save(Mockito.any());

        Response response = accountResource.updateAccount(1L,account);

        Assertions.assertEquals(204, response.getStatus());
    }

    @Test
    public void updateAccountNotExistedSuccessful(){
        Account account = new Account();
        account.setId(1L);

        Mockito.doReturn(Optional.empty()).when(accountService).getById(1L);
        Mockito.doReturn(Optional.of(account)).when(accountService).save(Mockito.any());

        Response response = accountResource.updateAccount(1L,account);

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    public void updateAccountNotExistedUnsuccessful(){
        Account account = new Account();
        account.setId(1L);

        Mockito.doReturn(Optional.empty()).when(accountService).getById(1L);
        Mockito.doReturn(Optional.empty()).when(accountService).save(Mockito.any());

        Response response = accountResource.updateAccount(1L,account);

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    public void testDelete(){
        Mockito.doReturn(true).when(accountService).deleteById(Mockito.any());

        Response response = accountResource.deleteAccount(1L);

        Assertions.assertEquals(204, response.getStatus());
    }
}
