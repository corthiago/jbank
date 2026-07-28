package com.thiago.jbank.service;

import com.thiago.jbank.controller.dto.TransferMoneyDto;
import com.thiago.jbank.entities.Transfer;
import com.thiago.jbank.entities.Wallet;
import com.thiago.jbank.exception.TransferException;
import com.thiago.jbank.exception.WalletNotFoundException;
import com.thiago.jbank.repository.TransferRepository;
import com.thiago.jbank.repository.WalletRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    public TransferService(TransferRepository transferRepository, WalletRepository walletRepository){
        this.transferRepository = transferRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void transferMoney(@Valid TransferMoneyDto dto) {

        var sender = walletRepository.findById(dto.sender())
                .orElseThrow(() -> new WalletNotFoundException("Sender does not exist"));

        var receiver = walletRepository.findById(dto.receiver())
                .orElseThrow(() -> new WalletNotFoundException("Receiver does not exist"));

        if(sender.getBalance().compareTo(dto.value()) == -1){
            throw new TransferException("Insufficient balance from sender");
        }

        updateWallets(dto, sender, receiver);

        persistTranfer(dto, receiver, sender);

    }

    private void updateWallets(TransferMoneyDto dto, Wallet sender, Wallet receiver) {
        sender.setBalance(sender.getBalance().subtract(dto.value()));
        walletRepository.save(sender);

        receiver.setBalance(receiver.getBalance().add(dto.value()));
        walletRepository.save(receiver);
    }

    private void persistTranfer(TransferMoneyDto dto, Wallet receiver, Wallet sender) {
        var transfer = new Transfer();
        transfer.setReceiver(receiver);
        transfer.setSender(sender);
        transfer.setAmount(dto.value());
        transfer.setDateTime(LocalDateTime.now());

        transferRepository.save(transfer);
    }
}
