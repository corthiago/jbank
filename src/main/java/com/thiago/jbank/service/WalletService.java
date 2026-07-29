package com.thiago.jbank.service;

import com.thiago.jbank.controller.dto.*;
import com.thiago.jbank.entities.Deposit;
import com.thiago.jbank.exception.StatementException;
import com.thiago.jbank.exception.WalletNotFoundException;
import com.thiago.jbank.repository.DepositRepository;
import com.thiago.jbank.repository.WalletRepository;
import com.thiago.jbank.entities.Wallet;
import com.thiago.jbank.exception.DeleteWalletException;
import com.thiago.jbank.exception.WalletDataAlreadyExistsException;
import com.thiago.jbank.repository.dto.StatementView;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public StatementDto getStatements(UUID walletId, Integer page, Integer pageSize) {

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        var pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "statement_date_time");

        var statements = walletRepository.findStatements(walletId.toString(), pageRequest)
                .map(view -> mapToDto(walletId, view));

        return new StatementDto(
                new WalletDto(wallet.getWalletId(), wallet.getDocumentNumber(), wallet.getName(), wallet.getEmail(), wallet.getBalance()),
                statements.getContent(),
                new PaginationDto(statements.getNumber(), statements.getSize(), statements.getTotalElements(), statements.getTotalPages())
        );

    }

    private StatementItemDto mapToDto(UUID walletId, StatementView view) {

        if(view.getType().equalsIgnoreCase("deposit")){
            return mapToDeposit(view);
        }

        if(view.getType().equalsIgnoreCase("transfer") && view.getWalletSender().equalsIgnoreCase(walletId.toString())){
            return mapToTransferSent(walletId, view);
        }

        if(view.getType().equalsIgnoreCase("transfer") && view.getWalletReceiver().equalsIgnoreCase(walletId.toString())){
            return maoToTransferReceived(walletId, view);
        }

        throw new StatementException("Invalid type " + view.getType());
    }

    private static StatementItemDto mapToDeposit(StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money deposit",
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.CREDIT
        );
    }

    private StatementItemDto mapToTransferSent(UUID walletId, StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money sent to " + view.getWalletReceiver(),
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.DEBIT
        );
    }

    private StatementItemDto maoToTransferReceived(UUID walletId, StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money received from " + view.getWalletSender(),
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.CREDIT
        );
    }
}
