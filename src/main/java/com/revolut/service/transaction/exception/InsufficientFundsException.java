package com.revolut.service.transaction.exception;

import java.math.BigDecimal;

/**
 * Represents transaction processing exception
 * should be used if source account do not have enough funds to transfer
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId, BigDecimal balance, BigDecimal transactionAmount){
        super("Can`t charge account "+accountId+" for amount: "+transactionAmount+", account balance: "+balance);
    }

}
