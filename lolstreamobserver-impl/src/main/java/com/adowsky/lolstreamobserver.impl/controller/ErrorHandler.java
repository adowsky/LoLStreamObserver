package com.adowsky.lolstreamobserver.impl.controller;

import com.adowsky.lolstreamobserver.impl.validation.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

@ControllerAdvice
class ErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorMessage messageNotReadable(HttpMessageNotReadableException ex){
        return new ErrorMessage("Message Not Readable", ex.getMessage());
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity poop(HttpStatusCodeException e){
        LOGGER.error("error", e);
        return new ResponseEntity<>(new ErrorMessage("a", "aa"), e.getStatusCode());
    }
}
