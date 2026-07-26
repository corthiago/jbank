package com.thiago.jbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class WalletDataAlreadyExistsException extends JBankException{

    private final String detail;

    public WalletDataAlreadyExistsException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd  = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);

        pd.setTitle("Wallet data already existis");
        pd.setDetail(detail);

        return pd;
    }
}
