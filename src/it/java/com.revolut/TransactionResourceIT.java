package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.database.entity.Transaction;
import com.revolut.database.entity.TransactionStatus;
import com.revolut.database.repository.account.AccountRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
public class TransactionResourceIT {
    private static final String TRANSACTIONS_ENDPOINT = "/transactions";
    private static final String ACCOUNTS_ENDPOINT = "/accounts";
    @Inject
    AccountRepository accountRepository;

    @BeforeEach
    public void init() {
        Account billGates = new Account();
        billGates.setId(1L);
        billGates.setName("Bill Gates");
        billGates.setBalance(new BigDecimal("1000000.00"));

        Account elonMusk = new Account();
        elonMusk.setId(2L);
        elonMusk.setName("Elon Musk");
        elonMusk.setBalance(new BigDecimal("200000.00"));

        accountRepository.save(billGates);
        accountRepository.save(elonMusk);
    }

    @Test
    public void testAccounts() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(TRANSACTIONS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$.size()", greaterThanOrEqualTo(1))
                .and().body("[0].debit", is(1))
                .and().body("[0].credit", is(2));

    }

    @Test
    public void particularTransaction() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(TRANSACTIONS_ENDPOINT + "/1")
                .then()
                .statusCode(200)
                .and().body("debit", is(1))
                .and().body("credit", is(2))
                .and().body("amount", is(new BigDecimal("100.00")));
    }

    @Test
    public void particularTransactionNotFound() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(TRANSACTIONS_ENDPOINT + "/10")
                .then()
                .statusCode(404);
    }

    @Test
    public void postSuccessfulTransaction() {
        Transaction transaction = new Transaction();
        transaction.setDebit(1L);
        transaction.setCredit(2L);
        transaction.setAmount(new BigDecimal("500000.00"));

        Response response = given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .post(TRANSACTIONS_ENDPOINT);
        response.then()
                .statusCode(201);

        String location = response.getHeader("Location");
        String newId = location.substring(location.lastIndexOf("/") + 1);

        given().when()
                .get(TRANSACTIONS_ENDPOINT + "/" + newId)
        .then().statusCode(200)
                .and().body("status",is(TransactionStatus.SUCCESSFUL.toString()));

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .get(ACCOUNTS_ENDPOINT + "/1")
                .then().statusCode(200)
                .and().body("balance",is(new BigDecimal("500000.00")));
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .get(ACCOUNTS_ENDPOINT + "/2")
                .then().statusCode(200)
                .and().body("balance",is(new BigDecimal("700000.00")));
    }

    @Test
    public void postUnsuccessfulTransaction(){
        Transaction transaction = new Transaction();
        transaction.setDebit(1L);
        transaction.setCredit(2L);
        transaction.setAmount(new BigDecimal("1500000.00"));

        Response response = given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(transaction)
                .when()
                .post(TRANSACTIONS_ENDPOINT);
        response.then()
                .statusCode(201);

        String location = response.getHeader("Location");
        String newId = location.substring(location.lastIndexOf("/") + 1);
        String data = TRANSACTIONS_ENDPOINT + "/" + newId;
        given().when()
                .get(TRANSACTIONS_ENDPOINT + "/" + newId)
                .then().statusCode(200)
                .and().body("status",is(TransactionStatus.UNSUCCESSFUL.toString()));

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .get(ACCOUNTS_ENDPOINT + "/1")
                .then().statusCode(200)
                .and().body("balance",is(new BigDecimal("1000000.00")));
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .get(ACCOUNTS_ENDPOINT + "/2")
                .then().statusCode(200)
                .and().body("balance",is(new BigDecimal("200000.00")));
    }

    @Test
    public void postIncorrectTransaction() {
        String json = "{\"inccorrect\"}";

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(TRANSACTIONS_ENDPOINT)
                .then()
                .statusCode(400);
    }

    @Test
    public void postEmptyTransaction() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when()
                .post(TRANSACTIONS_ENDPOINT)
                .then()
                .statusCode(400);
    }
}
