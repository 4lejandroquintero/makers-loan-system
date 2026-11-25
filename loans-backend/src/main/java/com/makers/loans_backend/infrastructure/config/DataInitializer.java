package com.makers.loans_backend.infrastructure.config;

import com.makers.loans_backend.domain.model.Loan;
import com.makers.loans_backend.domain.model.LoanStatus;
import com.makers.loans_backend.domain.model.Role;
import com.makers.loans_backend.domain.model.User;
import com.makers.loans_backend.domain.port.out.LoanRepositoryPort;
import com.makers.loans_backend.domain.port.out.UserRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepositoryPort userRepository,
                                   LoanRepositoryPort loanRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("usuario@test.com")) {
                User user = userRepository.save(new User(
                        null,
                        "Usuario",
                        "usuario@test.com",
                        passwordEncoder.encode("123"),
                        Role.ROLE_USER
                ));

                loanRepository.save(new Loan(
                        null,
                        new BigDecimal("1000"),
                        12,
                        LoanStatus.PENDING,
                        LocalDateTime.now(),
                        user.getId(),
                        user.getEmail()
                ));

                loanRepository.save(new Loan(
                        null,
                        new BigDecimal("2000"),
                        24,
                        LoanStatus.APPROVED,
                        LocalDateTime.now().minusDays(1),
                        user.getId(),
                        user.getEmail()
                ));
            }

            if (!userRepository.existsByEmail("admin@test.com")) {
                userRepository.save(new User(
                        null,
                        "Admin Admin",
                        "admin@test.com",
                        passwordEncoder.encode("123"),
                        Role.ROLE_ADMIN
                ));
            }
        };
    }
}
