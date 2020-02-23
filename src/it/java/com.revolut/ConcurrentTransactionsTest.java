package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.database.entity.Transaction;
import com.revolut.database.repository.account.AccountRepository;
import com.revolut.resource.AccountResource;
import com.revolut.service.account.AccountService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

@QuarkusTest
public class ConcurrentTransactionsTest {
    private static final Logger logger = Logger.getLogger(ConcurrentTransactionsTest.class.getName());
    private static final String TRANSACTIONS_ENDPOINT = "/transactions";
    private static final Integer THREAD_COUNT = 100;

    @Inject
    AccountRepository accountRepository;
    @Inject
    AccountService accountService;

    @Test
    public void concurrentExecute() throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(THREAD_COUNT);

       ImmutablePair<Long,Long> accountIds = prepareTestData();
       Long billGatesId = accountIds.getLeft();
       Long elonMuskId = accountIds.getRight();


        IntStream.range(0, THREAD_COUNT)
                .forEach(i -> new Thread(new TransactionTask(startSignal, doneSignal,billGatesId,elonMuskId)).start());

        startSignal.countDown();
        doneSignal.await();


        Account billGates = accountRepository.getById(billGatesId);
        Account elonMusk = accountRepository.getById(elonMuskId);

        logger.debug("Bill: "+billGates.getName()+" "+billGates.getBalance());
        logger.debug("Elon: "+elonMusk.getName()+" "+elonMusk.getBalance());
        Assertions.assertEquals(0, billGates.getBalance().compareTo(BigDecimal.ZERO), "Bill Gates balance is 0");
        Assertions.assertEquals(0, elonMusk.getBalance().compareTo(new BigDecimal("1200000.00")), "Elon Musk balance is 1200000");
    }

    private class TransactionTask implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        private final Long fromId;
        private final Long toId;

        TransactionTask(CountDownLatch startSignal, CountDownLatch doneSignal, Long fromId, Long toId) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.fromId = fromId;
            this.toId = toId;
        }

        public void run() {
            try {
                startSignal.await();
                executeTransaction();
                doneSignal.countDown();
            } catch (InterruptedException e) {
                logger.error("", e);
            }
        }

        void executeTransaction() {
          //  logger.error("execute transaction");
            Transaction transaction = new Transaction();
            transaction.setDebit(fromId);
            transaction.setCredit(toId);
            transaction.setAmount(new BigDecimal("100000.00"));

            Response response = given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                    .contentType(ContentType.JSON)
                    .body(transaction)
                    .when()
                    .post(TRANSACTIONS_ENDPOINT);
            response.then()
                    .statusCode(201);
        }
    }

    private ImmutablePair<Long,Long> prepareTestData(){
        Account billGates = new Account();
        billGates.setName("Bill Gates 2");
        billGates.setBalance(new BigDecimal("1000000.00"));

        Account elonMusk = new Account();
        elonMusk.setName("Elon Musk 2");
        elonMusk.setBalance(new BigDecimal("200000.00"));

        billGates = accountRepository.save(billGates);
        elonMusk = accountRepository.save(elonMusk);
        Long billGatesId = billGates.getId();
        Long elonMuskId = elonMusk.getId();

        return ImmutablePair.of(billGatesId,elonMuskId);
    }
}
