package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.database.repository.account.AccountRepository;
import com.revolut.service.account.AccountServiceImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
we need to inject dumb objects due to to https://github.com/quarkusio/quarkus/issues/1724
without them we are getting error and can not use mockito mocking;
 */
@QuarkusTest
public class AccountServiceImplTest {

    @Inject
    AccountRepository dumbAccountRepository;
    @Inject
    AccountServiceImpl dumbAccountService;

    @Mock
    AccountRepository accountRepository;
    @InjectMocks
    AccountServiceImpl accountService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findAllReturnList() {
        Mockito.doReturn(new ArrayList<>()).when(accountRepository).getAll();

        List<Account> accounts = accountService.findAll();

        Mockito.verify(accountRepository, Mockito.only()).getAll();
        Assertions.assertNotNull(accounts);
    }

    @Test
    public void getByIdReturnOptionalWithAccount() {
        Account returnAccount = new Account();
        returnAccount.setId(1L);
        returnAccount.setName("John");

        Mockito.doReturn(returnAccount).when(accountRepository).getById(1L);

        Optional<Account> account = accountService.getById(1L);

        Assertions.assertTrue(account.isPresent());
        Assertions.assertEquals("John", account.get().getName());
    }

    @Test
    public void getByIdReturnEmptyOptional() {
        Mockito.doThrow(new NoResultException()).when(accountRepository).getById(1L);

        Optional<Account> account = accountService.getById(1L);

        //as
        Assertions.assertFalse(account.isPresent());
    }

    @Test
    public void saveReturnOptionalWithAccount() {
        Account returnAccount = new Account();
        returnAccount.setId(1L);
        returnAccount.setName("John");

        Mockito.doReturn(returnAccount).when(accountRepository).save(Mockito.any());

        Optional<Account> account = accountService.save(returnAccount);

        Assertions.assertTrue(account.isPresent());
        Assertions.assertEquals("John", account.get().getName());
    }

    @Test
    public void saveReturnEmptyOptional() {
        Mockito.doReturn(null).when(accountRepository).save(Mockito.any());

        Optional<Account> account = accountService.save(new Account());

        Assertions.assertTrue(account.isEmpty());
    }

    @Test
    public void deleteSuccessful(){
        Mockito.doReturn(1L).when(accountRepository).deleteById(Mockito.any());

        boolean result = accountService.deleteById(1L);

        Assertions.assertTrue(result);
    }

    @Test
    public void deleteUnsuccessful(){
        Mockito.doReturn(0L).when(accountRepository).deleteById(Mockito.any());

        boolean result = accountService.deleteById(1L);

        Assertions.assertFalse(result);
    }
}
