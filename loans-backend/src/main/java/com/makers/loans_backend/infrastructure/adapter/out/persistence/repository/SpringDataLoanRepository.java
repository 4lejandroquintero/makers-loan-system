package com.makers.loans_backend.infrastructure.adapter.out.persistence.repository;

import com.makers.loans_backend.infrastructure.adapter.out.persistence.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataLoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByUserEmail(String email);
}