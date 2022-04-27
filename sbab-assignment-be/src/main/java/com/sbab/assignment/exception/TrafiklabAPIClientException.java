package com.sbab.assignment.exception;

public class TrafiklabAPIClientException extends RuntimeException{

    public TrafiklabAPIClientException(String errorMessage, Throwable err) {
        super (errorMessage, err);
    }

}
