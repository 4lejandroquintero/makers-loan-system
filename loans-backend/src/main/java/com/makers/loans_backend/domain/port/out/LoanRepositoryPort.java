package com.makers.loans_backend.domain.port.out;

import com.makers.loans_backend.domain.model.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanRepositoryPort {
    Loan save(Loan loan);
    Optional<Loan> findById(Long id);
    List<Loan> findByUserEmail(String email);
    List<Loan> findAll();
    void deleteById(Long id);
}
