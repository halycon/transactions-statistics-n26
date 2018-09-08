package com.n26.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.n26.domain.Transaction;
import com.n26.service.impl.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@ControllerAdvice
@RestController
public class TransactionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/transactions", method = {RequestMethod.POST} , produces = "application/json")
    private ResponseEntity<Void> putTransaction(@RequestBody Transaction transaction){
        if(!transactionService.validateForOlderTransactionTimestamp(transaction, Instant.now()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
        if(!transactionService.validateForFutureTransactionTimestampAndAmount(transaction, Instant.now()))
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        else{
            transactionService.saveTransaction(transaction);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/transactions", method = {RequestMethod.DELETE} , produces = "application/json")
    private ResponseEntity<Void> removeTransactions(){

        transactionService.removeTransactions();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<Void> httpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        if(ex.getCause() instanceof InvalidFormatException)
            return  new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
