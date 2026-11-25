import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { Loan, LoanStatus } from '../../../core/models/models';
import { AuthService } from '../../../core/services/auth';
import { LoanService } from '../../../core/services/loan';

@Component({
  selector: 'app-user-loans',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-loans.html',
  styleUrl: './user-loans.css'
})
export class UserLoans implements OnInit {
  loanForm: FormGroup;
  loans$: Observable<Loan[]>;
  userName = 'Usuario';
  submitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private loanService: LoanService
  ) {
    this.loans$ = this.loanService.loans$;
    this.userName = this.authService.getCurrentUser()?.name ?? 'Usuario';

    this.loanForm = this.fb.group({
      amount: [null, [Validators.required, Validators.min(100)]],
      termMonths: [12, [Validators.required, Validators.min(1), Validators.max(120)]]
    });
  }

  ngOnInit(): void {
    this.loanService.loadMyLoans().subscribe();
  }

  onRequestLoan(): void {
    if (this.loanForm.invalid) {
      this.loanForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    this.loanService.requestLoan(this.loanForm.value).subscribe({
      next: () => {
        this.submitting = false;
        this.loanForm.reset({ amount: null, termMonths: 12 });
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err.error?.message || 'No se pudo enviar la solicitud.';
      }
    });
  }

  translateStatus(status: LoanStatus): string {
    const labels: Record<LoanStatus, string> = {
      PENDING: 'pendiente',
      APPROVED: 'aprobado',
      REJECTED: 'rechazado'
    };
    return labels[status];
  }

  logout(): void {
    this.authService.logout();
  }
}