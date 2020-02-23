package com.revolut.resource;

import com.revolut.database.entity.Transaction;
import com.revolut.service.transaction.TransactionService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

/**
 * Represent RESTful endpoint for operations with transaction entities
 */
@Path("/transactions")
public class TransactionResource {

    @Context
    UriInfo uriInfo;
    @Inject
    TransactionService transactionService;

    /**
     * Idempotent get request
     * retrieves information about all transactions in system
     * @return HTTP 200 with list of all transactions in JSON format
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions() {
        return Response.ok(transactionService.findAll()).build();
    }

    /**
     * Idempotent get request
     * retrieves information about specific transaction
     * @param id of transaction
     * @return HTTP 200 if transaction found, transaction info in JSON format
     *         HTTP 404 if transaction not found
     *
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getTransaction(@PathParam("id") Long id) {
        return transactionService.getById(id)
                .map(transaction -> Response.ok(transaction).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Not idempotent request
     * save new transaction entity
     * @param transaction entity in JSON format
     * @return HTTP 201 if transaction was created, populates 'Location' header
     *         HTTP 400 if data was empty, or something went wrong
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTransaction(Transaction transaction) {
        if(transaction == null) return Response.status(Response.Status.BAD_REQUEST).build();
        Optional<Transaction> saved = transactionService.save(transaction);
        return saved.map(savedTransaction -> Response.created(uriInfo.getBaseUriBuilder()
                .path("/transactions/{id}")
                .build(savedTransaction.getId())).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST).build());
    }

}
