package com.makers.loans_backend.application.service;

import com.makers.loans_backend.domain.exception.ResourceNotFoundException;
import com.makers.loans_backend.domain.model.Loan;
import com.makers.loans_backend.domain.model.LoanStatus;
import com.makers.loans_backend.domain.model.User;
import com.makers.loans_backend.domain.port.in.LoanUseCase;
import com.makers.loans_backend.domain.port.out.LoanRepositoryPort;
import com.makers.loans_backend.domain.port.out.UserRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService implements LoanUseCase {

    private final LoanRepositoryPort loanRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public LoanService(LoanRepositoryPort loanRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.loanRepositoryPort = loanRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loansByUser", "allLoans", "loanById"}, allEntries = true)
    public Loan requestLoan(String userEmail, BigDecimal amount, Integer termMonths) {
        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userEmail));

        Loan loan = new Loan(
                null,
                amount,
                termMonths,
                LoanStatus.PENDING,
                LocalDateTime.now(),
                user.getId(),
                user.getEmail()
        );
        return loanRepositoryPort.save(loan);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "loansByUser", key = "#userEmail")
    public List<Loan> getLoansByUser(String userEmail) {
        return loanRepositoryPort.findByUserEmail(userEmail);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allLoans")
    public List<Loan> getAllLoans() {
        return loanRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "loanById", key = "#id")
    public Loan getLoanById(Long id) {
        return loanRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loansByUser", "allLoans", "loanById"}, allEntries = true)
    public Loan approveLoan(Long id) {
        Loan loan = loanRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));
        loan.approve();
        return loanRepositoryPort.save(loan);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loansByUser", "allLoans", "loanById"}, allEntries = true)
    public Loan rejectLoan(Long id) {
        Loan loan = loanRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + id));
        loan.reject();
        return loanRepositoryPort.save(loan);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"loansByUser", "allLoans", "loanById"}, allEntries = true)
    public void deleteLoan(Long id) {
        if (loanRepositoryPort.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Préstamo no encontrado con id: " + id);
        }
        loanRepositoryPort.deleteById(id);
    }
}