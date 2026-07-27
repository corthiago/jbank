package com.thiago.jbank.controller;

import com.thiago.jbank.controller.dto.CreateWalletDto;
import com.thiago.jbank.controller.dto.DepositMoneyDto;
import com.thiago.jbank.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Void> createWallet(@RequestBody @Valid CreateWalletDto dto){

        var wallet = walletService.createWallet(dto);

        return ResponseEntity.created(URI.create("/wallets/" + wallet.getWalletId().toString())).build();

    }

    @DeleteMapping(path = "/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable(name = "walletId") UUID walletId){

        var deleted = walletService.deleteWallet(walletId);

        return deleted ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();

    }

    @PostMapping(path = "/{walletId}/deposits")
    public ResponseEntity<Void> deposityMoney(@PathVariable(name = "walletId") UUID walletId,
                                              @RequestBody @Valid DepositMoneyDto dto,
                                              HttpServletRequest request){

        var ipAddress = request.getAttribute("x-user-ip").toString();

        walletService.depositMoney(walletId, dto, ipAddress);

        return ResponseEntity.ok().build();
    }

}
