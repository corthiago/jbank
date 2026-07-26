package com.thiago.jbank;

import com.thiago.jbank.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByDocumentNumberOrEmail(String documentNumber, String email);

}
