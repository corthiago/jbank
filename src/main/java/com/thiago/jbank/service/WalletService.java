package com.thiago.jbank.service;

import com.thiago.jbank.WalletRepository;
import com.thiago.jbank.controller.dto.CreateWalletDto;
import com.thiago.jbank.entities.Wallet;
import com.thiago.jbank.exception.WalletDataAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    public Wallet createWallet(CreateWalletDto dto) {
        var wallet = walletRepository.findByDocumentNumberOrEmail(dto.documentNumber(), dto.email());
        if(wallet.isPresent()){
            throw new WalletDataAlreadyExistsException("Document Number or Email already exists");
        }

        var newWallet = new Wallet();
        newWallet.setDocumentNumber(dto.documentNumber());
        newWallet.setEmail(dto.email());
        newWallet.setName(dto.name());
        newWallet.setBalance(BigDecimal.ZERO);

        return walletRepository.save(newWallet);
    }
}
