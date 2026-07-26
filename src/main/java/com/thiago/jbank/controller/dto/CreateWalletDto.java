package com.thiago.jbank.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateWalletDto(@NotBlank String documentNumber,
                              @Email @NotBlank String email,
                              @NotBlank String name) {
}
