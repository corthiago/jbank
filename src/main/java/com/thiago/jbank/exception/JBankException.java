package com.thiago.jbank.exception;

public class JBankException extends RuntimeException{

    public JBankException(String message) {
        super(message);
    }

    public JBankException(Throwable cause) {
        super(cause);
    }
}
