package com.thiago.jbank.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletDto(UUID walletId,
                        String documentNumber,
                        String name,
                        String email,
                        BigDecimal balance) {
}
