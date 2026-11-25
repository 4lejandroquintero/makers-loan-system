export type Role = 'ROLE_USER' | 'ROLE_ADMIN';
export type LoanStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface AuthResponse {
  token: string;
  email: string;
  name: string;
  role: Role;
}

export interface Loan {
  id: number;
  amount: number;
  termMonths: number;
  status: LoanStatus;
  createdAt: string;
  userId: number;
  userEmail: string;
}

export interface LoanRequest {
  amount: number;
  termMonths: number;
}