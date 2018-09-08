package com.n26.controller;

import com.n26.domain.Transaction;
import com.n26.service.impl.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class TransactionController {

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
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/transactions", method = {RequestMethod.DELETE} , produces = "application/json")
    private ResponseEntity<Void> removeTransactions(){

        transactionService.removeTransactions();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
