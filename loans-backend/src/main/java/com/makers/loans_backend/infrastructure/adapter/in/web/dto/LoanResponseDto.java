package com.makers.loans_backend.infrastructure.adapter.in.web.dto;

import com.makers.loans_backend.domain.model.Loan;
import com.makers.loans_backend.domain.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponseDto(
        Long id,
        BigDecimal amount,
        Integer termMonths,
        LoanStatus status,
        LocalDateTime createdAt,
        Long userId,
        String userEmail
) {
    public static LoanResponseDto fromDomain(Loan loan) {
        return new LoanResponseDto(
                loan.getId(),
                loan.getAmount(),
                loan.getTermMonths(),
                loan.getStatus(),
                loan.getCreatedAt(),
                loan.getUserId(),
                loan.getUserEmail()
        );
    }
}

