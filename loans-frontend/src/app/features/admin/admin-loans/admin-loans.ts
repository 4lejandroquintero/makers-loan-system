import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { Loan, LoanStatus } from '../../../core/models/models';
import { AuthService } from '../../../core/services/auth';
import { LoanService } from '../../../core/services/loan';

@Component({
  selector: 'app-admin-loans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-loans.html',
  styleUrl: './admin-loans.css'
})
export class AdminLoans implements OnInit {
  loans$: Observable<Loan[]>;
  adminName = 'Admin Admin';

  constructor(
    private authService: AuthService,
    private loanService: LoanService
  ) {
    this.loans$ = this.loanService.loans$;
    this.adminName = this.authService.getCurrentUser()?.name ?? 'Admin Admin';
  }

  ngOnInit(): void {
    this.loanService.loadAllLoans().subscribe();
  }

  approve(id: number): void {
    this.loanService.approveLoan(id).subscribe();
  }

  reject(id: number): void {
    this.loanService.rejectLoan(id).subscribe();
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