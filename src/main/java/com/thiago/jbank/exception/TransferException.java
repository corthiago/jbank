package com.thiago.jbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class TransferException extends JBankException {

    private final String detail;

    public TransferException(String detail) {
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var pd  = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_CONTENT);

        pd.setTitle("Transfer not allowed");
        pd.setDetail(detail);

        return pd;
    }
}
