package com.thiago.jbank.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositMoneyDto(@NotNull @DecimalMin("0.01") BigDecimal value) {
}
