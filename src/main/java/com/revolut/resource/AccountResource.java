package com.revolut.resource;

import com.revolut.database.entity.Account;
import com.revolut.service.account.AccountService;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.Optional;

/**
 * Represent RESTful endpoint for operations with account entities
 */
@Path("/accounts")
public class AccountResource {
    private static final Logger logger = Logger.getLogger(AccountResource.class.getName());

    @Context
    UriInfo uriInfo;
    @Inject
    AccountService accountService;

    /**
     * Idempotent get request
     * retrieves information about all accounts in system
     * @return HTTP 200 with list of all accounts in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        return Response.ok(accountService.findAll()).build();
    }

    /**
     * Idempotent get request
     * retrieves information about specific account
     * @param id of account
     * @return HTTP 200 if account found, account info in JSON format
     *         HTTP 404 if account not found
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getAccount(@PathParam Long id) {
        return accountService.getById(id)
                .map(account -> Response.ok(account).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Not idempotent request
     * save new account entity
     * @param account entity in JSON format
     * @return HTTP 201 if account was created, populates 'Location' header
     *         HTTP 400 if data was empty, or something went wrong
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
        if(account == null) return Response.status(Response.Status.BAD_REQUEST).build();
        Optional<Account> saved = accountService.save(account);
        return saved.map(savedAccount -> Response.created(uriInfo.getBaseUriBuilder()
                .path("/accounts/{id}")
                .build(savedAccount.getId())).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).build());
    }

    /**
     * Idempotent request
     * tries to update account entity with given id
     * if there is no entity with given id, then this method creates it
     * @param id of account entity to update
     * @param account data to update
     * @return HTTP 201 if account was created, populates 'Location' header
     *         HTTP 204 if account was updated
     *         HTTP 400 if account param was empty or something went wrong
     */
    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("id") Long id, Account account) {
        if(account == null) return Response.status(Response.Status.BAD_REQUEST).build();
        Optional<Account> existed = accountService.getById(id);
        if (existed.isPresent()) {
            logger.debug("existed");
            account.setId(id);
            accountService.save(account);
            return Response.noContent().build();
        } else {
            logger.debug("not existed");
            account.setId(id);
            Optional<Account> saved = accountService.save(account);
            return saved.map(savedAccount ->
                    Response.created(uriInfo.getBaseUriBuilder()
                            .path("/accounts/{id}")
                            .build(savedAccount.getId())).build()

            ).orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).build());
        }
    }

    /**
     * Idempotent request
     * tries to delete account with given id
     * @param id of account ot delete
     * @return HTTP 204
     */
    @Path("/{id}")
    @DELETE
    public Response deleteAccount(@PathParam("id") Long id) {
        accountService.deleteById(id);
        return Response.noContent().build();
    }

}
