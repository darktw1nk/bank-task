package com.revolut;

import com.revolut.database.entity.Account;
import com.revolut.database.repository.account.AccountRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;


@QuarkusTest
public class AccountResourceIT {
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

    @AfterEach
    public void afterEach() {
        // accountRepository.deleteAllAccounts();
    }

    @Test
    public void testAccounts() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(ACCOUNTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$.size()", is(2),
                        "name", containsInAnyOrder("Bill Gates", "Elon Musk"))
                .and().body("[0].balance", is(new BigDecimal("1000000.00")))
                .and().body("[1].balance", is(new BigDecimal("200000.00")));

    }

    @Test
    public void particularAccount() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(ACCOUNTS_ENDPOINT + "/1")
                .then()
                .statusCode(200)
                .and().body("name", is("Bill Gates"))
                .and().body("balance", is(new BigDecimal("1000000.00")));
    }

    @Test
    public void particularAccountNotFound() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(ACCOUNTS_ENDPOINT + "/10")
                .then()
                .statusCode(404);
    }

    @Test
    public void postCorrectAccount() {
        Account account = new Account();
        account.setName("Virual");
        account.setBalance(new BigDecimal("100.00"));

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(account)
                .when()
                .post(ACCOUNTS_ENDPOINT)
                .then()
                .statusCode(201);
    }

    @Test
    public void postIncorrectAccount() {
        String json = "{\"inccorrect\"}";

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(ACCOUNTS_ENDPOINT)
                .then()
                .statusCode(400);
    }

    @Test
    public void postEmptyAccount() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when()
                .post(ACCOUNTS_ENDPOINT)
                .then()
                .statusCode(400);
    }

    @Test
    public void putExistedAccount() {
        Account account = new Account();
        account.setName("Not Bill Gates");
        account.setBalance(new BigDecimal("100.00"));

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(account)
                .when()
                .put(ACCOUNTS_ENDPOINT + "/1")
                .then()
                .statusCode(204);

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(ACCOUNTS_ENDPOINT + "/1")
                .then()
                .statusCode(200)
                .and().body("name", is("Not Bill Gates"))
                .and().body("balance", is(new BigDecimal("100.00")));
    }

    @Test
    public void putNotExistedAccount() {
        Account account = new Account();
        account.setName("Not Bill Gates");
        account.setBalance(new BigDecimal("100.00"));

        Response response = given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(account)
                .when()
                .put(ACCOUNTS_ENDPOINT + "/7");
        response.then()
                .statusCode(201);
        String location = response.getHeader("Location");
        String newId = location.substring(location.lastIndexOf("/") + 1);

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(ACCOUNTS_ENDPOINT + "/" + newId)
                .then()
                .statusCode(200)
                .and().body("name", is("Not Bill Gates"))
                .and().body("balance", is(new BigDecimal("100.00")));
    }

    @Test
    public void putIncorrectAccount() {
        String json = "{\"inccorrect\"}";

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .put(ACCOUNTS_ENDPOINT + "/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void putEmptyAccount() {
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when()
                .put(ACCOUNTS_ENDPOINT + "/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void deleteExisted() {
        Account billGates = new Account();
        billGates.setName("Bill Gates 2");
        billGates.setBalance(new BigDecimal("1000000.00"));
        billGates = accountRepository.save(billGates);

        Long newId = billGates.getId();

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .get(ACCOUNTS_ENDPOINT + "/"+newId)
                .then()
                .statusCode(200);

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .delete(ACCOUNTS_ENDPOINT + "/"+newId)
                .then()
                .statusCode(204);

        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get(ACCOUNTS_ENDPOINT + "/1"+newId)
                .then()
                .statusCode(404);
    }
}