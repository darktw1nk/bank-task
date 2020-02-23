package com.revolut.database.entity;

/**
 * Represents status of transaction in our system
 * it is the only field containing processing information
 * if transaction fails system do not persist fail reason
 */
public enum TransactionStatus {
    SUCCESSFUL, UNSUCCESSFUL
}
