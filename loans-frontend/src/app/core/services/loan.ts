import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Loan, LoanRequest } from '../models/models';

@Injectable({ providedIn: 'root' })
export class LoanService {
  private readonly apiUrl = 'http://localhost:8080/api/loans';

  private loansSubject = new BehaviorSubject<Loan[]>([]);
  loans$ = this.loansSubject.asObservable();

  constructor(private http: HttpClient) {}

  loadMyLoans(): Observable<Loan[]> {
    return this.http.get<Loan[]>(`${this.apiUrl}/my`).pipe(
      tap((loans) => this.loansSubject.next(loans))
    );
  }

  loadAllLoans(): Observable<Loan[]> {
    return this.http.get<Loan[]>(this.apiUrl).pipe(
      tap((loans) => this.loansSubject.next(loans))
    );
  }

  requestLoan(payload: LoanRequest): Observable<Loan> {
    return this.http.post<Loan>(this.apiUrl, payload).pipe(
      tap((created) => {
        this.loansSubject.next([created, ...this.loansSubject.value]);
      })
    );
  }

  approveLoan(id: number): Observable<Loan> {
    return this.http.patch<Loan>(`${this.apiUrl}/${id}/approve`, {}).pipe(
      tap((updated) => this.replaceLoanInState(updated))
    );
  }

  rejectLoan(id: number): Observable<Loan> {
    return this.http.patch<Loan>(`${this.apiUrl}/${id}/reject`, {}).pipe(
      tap((updated) => this.replaceLoanInState(updated))
    );
  }

  private replaceLoanInState(updated: Loan): void {
    const list = this.loansSubject.value.map((l) => (l.id === updated.id ? updated : l));
    this.loansSubject.next(list);
  }
}