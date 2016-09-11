package com.adowsky.lolstreamobserver.impl.controller;

import com.adowsky.lolstreamobserver.impl.validation.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorMessage messageNotReadable(HttpMessageNotReadableException ex){
        return new ErrorMessage("Message Not Readable", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpServerErrorException.class)
    public ErrorMessage serviceUnavailable(HttpMessageNotReadableException ex) {
        return new ErrorMessage("Service unavailable", ex.getMessage());
    }
}
