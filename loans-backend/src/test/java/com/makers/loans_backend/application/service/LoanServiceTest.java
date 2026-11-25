package com.makers.loans_backend.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.makers.loans_backend.domain.exception.ResourceNotFoundException;
import com.makers.loans_backend.domain.model.Loan;
import com.makers.loans_backend.domain.model.LoanStatus;
import com.makers.loans_backend.domain.model.Role;
import com.makers.loans_backend.domain.model.User;
import com.makers.loans_backend.domain.port.out.LoanRepositoryPort;
import com.makers.loans_backend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepositoryPort loanRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private Loan pendingLoan;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "Usuario", "usuario@test.com", "123", Role.ROLE_USER);
        pendingLoan = new Loan(
                10L,
                new BigDecimal("1000"),
                12,
                LoanStatus.PENDING,
                LocalDateTime.now(),
                1L,
                "usuario@test.com"
        );
    }

    @Test
    void requestLoan_ShouldCreatePendingLoan_WhenUserExists() {
        when(userRepositoryPort.findByEmail("usuario@test.com")).thenReturn(Optional.of(testUser));
        when(loanRepositoryPort.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = loanService.requestLoan("usuario@test.com", new BigDecimal("1500"), 12);

        assertNotNull(result);
        assertEquals(LoanStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("1500"), result.getAmount());
        assertEquals(12, result.getTermMonths());
        verify(loanRepositoryPort).save(any(Loan.class));
    }

    @Test
    void approveLoan_ShouldChangeStatusToApproved_WhenLoanIsPending() {
        when(loanRepositoryPort.findById(10L)).thenReturn(Optional.of(pendingLoan));
        when(loanRepositoryPort.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = loanService.approveLoan(10L);

        assertEquals(LoanStatus.APPROVED, result.getStatus());
        verify(loanRepositoryPort).save(pendingLoan);
    }

    @Test
    void rejectLoan_ShouldChangeStatusToRejected_WhenLoanIsPending() {
        when(loanRepositoryPort.findById(10L)).thenReturn(Optional.of(pendingLoan));
        when(loanRepositoryPort.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan result = loanService.rejectLoan(10L);

        assertEquals(LoanStatus.REJECTED, result.getStatus());
        verify(loanRepositoryPort).save(pendingLoan);
    }

    @Test
    void approveLoan_ShouldThrowException_WhenLoanIsNotPending() {
        pendingLoan.setStatus(LoanStatus.APPROVED);
        when(loanRepositoryPort.findById(10L)).thenReturn(Optional.of(pendingLoan));

        assertThrows(IllegalStateException.class, () -> loanService.approveLoan(10L));
        verify(loanRepositoryPort, never()).save(any());
    }

    @Test
    void getLoanById_ShouldThrowNotFound_WhenDoesNotExist() {
        when(loanRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanService.getLoanById(99L));
    }
}