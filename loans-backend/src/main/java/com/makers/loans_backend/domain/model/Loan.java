package com.makers.loans_backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loan {
    private Long id;
    private BigDecimal amount;
    private Integer termMonths;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private Long userId;
    private String userEmail;

    public Loan() {}

    public Loan(Long id, BigDecimal amount, Integer termMonths, LoanStatus status,
                LocalDateTime createdAt, Long userId, String userEmail) {
        this.id = id;
        this.amount = amount;
        this.termMonths = termMonths;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public void approve() {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden aprobar préstamos en estado pendiente");
        }
        this.status = LoanStatus.APPROVED;
    }

    public void reject() {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden rechazar préstamos en estado pendiente");
        }
        this.status = LoanStatus.REJECTED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
