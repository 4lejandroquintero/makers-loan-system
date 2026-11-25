package com.makers.loans_backend.infrastructure.adapter.in.web.controller;

import com.makers.loans_backend.domain.port.in.LoanUseCase;
import com.makers.loans_backend.infrastructure.adapter.in.web.dto.LoanRequestDto;
import com.makers.loans_backend.infrastructure.adapter.in.web.dto.LoanResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanUseCase loanUseCase;

    public LoanController(LoanUseCase loanUseCase) {
        this.loanUseCase = loanUseCase;
    }

    @PostMapping
    public ResponseEntity<LoanResponseDto> requestLoan(@Valid @RequestBody LoanRequestDto request,
                                                       Authentication authentication) {
        var loan = loanUseCase.requestLoan(authentication.getName(), request.amount(), request.termMonths());
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanResponseDto.fromDomain(loan));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LoanResponseDto>> getMyLoans(Authentication authentication) {
        var loans = loanUseCase.getLoansByUser(authentication.getName())
                .stream().map(LoanResponseDto::fromDomain).toList();
        return ResponseEntity.ok(loans);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        var loans = loanUseCase.getAllLoans()
                .stream().map(LoanResponseDto::fromDomain).toList();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDto> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(LoanResponseDto.fromDomain(loanUseCase.getLoanById(id)));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDto> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(LoanResponseDto.fromDomain(loanUseCase.approveLoan(id)));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDto> rejectLoan(@PathVariable Long id) {
        return ResponseEntity.ok(LoanResponseDto.fromDomain(loanUseCase.rejectLoan(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanUseCase.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
