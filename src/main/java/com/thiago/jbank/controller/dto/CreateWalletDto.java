package com.thiago.jbank.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateWalletDto(@NotBlank String documentNumber,
                              @Email @NotBlank String email,
                              @NotBlank String name) {
}
