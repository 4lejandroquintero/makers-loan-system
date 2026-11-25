package com.makers.loans_backend.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LoanRequestDto(
        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "100.00", message = "El monto mínimo es 100")
        BigDecimal amount,

        @NotNull(message = "El plazo es obligatorio")
        @Min(value = 1, message = "El plazo mínimo es 1 mes")
        @Max(value = 120, message = "El plazo máximo es 120 meses")
        Integer termMonths
) {}