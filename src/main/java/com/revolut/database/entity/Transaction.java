package com.revolut.database.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Represents bank transaction in system
 * logic of this system is that transaction should be processed on save
 * and one of transaction statuses should be set
 * @see TransactionStatus
 * since the system is quite simple also directly serializes to JSON in Controller layer
 */
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long debit;
    private Long credit;
    private BigDecimal amount;
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDebit() {
        return debit;
    }

    public void setDebit(Long debit) {
        this.debit = debit;
    }

    public Long getCredit() {
        return credit;
    }

    public void setCredit(Long credit) {
        this.credit = credit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
