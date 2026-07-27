package com.thiago.jbank.service;

import com.thiago.jbank.entities.Deposit;
import com.thiago.jbank.exception.WalletNotFoundException;
import com.thiago.jbank.repository.DepositRepository;
import com.thiago.jbank.repository.WalletRepository;
import com.thiago.jbank.controller.dto.CreateWalletDto;
import com.thiago.jbank.controller.dto.DepositMoneyDto;
import com.thiago.jbank.entities.Wallet;
import com.thiago.jbank.exception.DeleteWalletException;
import com.thiago.jbank.exception.WalletDataAlreadyExistsException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final DepositRepository depositRepository;

    public WalletService(WalletRepository walletRepository, DepositRepository depositRepository) {
        this.walletRepository = walletRepository;
        this.depositRepository = depositRepository;
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

    public boolean deleteWallet(UUID walletId) {
        var wallet = walletRepository.findById(walletId);

        if(wallet.isPresent()) {
            if(wallet.get().getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new DeleteWalletException("The balance must zero for wallet deletion");
            }
            walletRepository.deleteById(walletId);
        }

        return wallet.isPresent();
    }

    @Transactional
    public void depositMoney(UUID walletId, DepositMoneyDto dto, String ipAddress) {

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("There is not wallet with this id"));

        var deposit = new Deposit();
        deposit.setWallet(wallet);
        deposit.setAmount(dto.value());
        deposit.setDateTime(LocalDateTime.now());
        deposit.setIpAddress(ipAddress);

        depositRepository.save(deposit);

        wallet.setBalance(wallet.getBalance().add(dto.value()));

        walletRepository.save(wallet);
    }
}
