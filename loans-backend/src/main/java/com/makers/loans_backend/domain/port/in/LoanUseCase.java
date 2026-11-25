package com.makers.loans_backend.domain.port.in;

import com.makers.loans_backend.domain.model.Loan;
import java.math.BigDecimal;
import java.util.List;

public interface LoanUseCase {
    Loan requestLoan(String userEmail, BigDecimal amount, Integer termMonths);
    List<Loan> getLoansByUser(String userEmail);
    List<Loan> getAllLoans();
    Loan getLoanById(Long id);
    Loan approveLoan(Long id);
    Loan rejectLoan(Long id);
    void deleteLoan(Long id);
}