package com.thiago.jbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

public class StatementException extends JBankException{

    private final String detail;

    public StatementException(String detail){
        super(detail);
        this.detail = detail;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        pd.setTitle("Invalid statement scenario");
        pd.setDetail(detail);

        return pd;
    }
}
