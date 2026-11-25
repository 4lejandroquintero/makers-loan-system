package com.makers.loans_backend.infrastructure.adapter.out.persistence;

import com.makers.loans_backend.domain.exception.ResourceNotFoundException;
import com.makers.loans_backend.domain.model.Loan;
import com.makers.loans_backend.domain.port.out.LoanRepositoryPort;
import com.makers.loans_backend.infrastructure.adapter.out.persistence.entity.LoanEntity;
import com.makers.loans_backend.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.makers.loans_backend.infrastructure.adapter.out.persistence.repository.SpringDataLoanRepository;
import com.makers.loans_backend.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LoanPersistenceAdapter implements LoanRepositoryPort {

    private final SpringDataLoanRepository loanRepository;
    private final SpringDataUserRepository userRepository;

    public LoanPersistenceAdapter(SpringDataLoanRepository loanRepository,
                                  SpringDataUserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Loan save(Loan loan) {
        UserEntity userEntity = userRepository.findById(loan.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + loan.getUserId()));

        LoanEntity entity = new LoanEntity();
        entity.setId(loan.getId());
        entity.setAmount(loan.getAmount());
        entity.setTermMonths(loan.getTermMonths());
        entity.setStatus(loan.getStatus());
        entity.setCreatedAt(loan.getCreatedAt());
        entity.setUser(userEntity);

        return toDomain(loanRepository.save(entity));
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Loan> findByUserEmail(String email) {
        return loanRepository.findByUserEmail(email).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

    private Loan toDomain(LoanEntity entity) {
        return new Loan(
                entity.getId(),
                entity.getAmount(),
                entity.getTermMonths(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUser().getId(),
                entity.getUser().getEmail()
        );
    }
}