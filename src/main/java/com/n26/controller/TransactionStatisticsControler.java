package com.n26.controller;

import com.n26.domain.TransactionStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class TransactionStatisticsControler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/statistics", method = {RequestMethod.GET} , produces = "application/json")
    private ResponseEntity<TransactionStatistics> statistics(){

        Instant instantOfRequest = Instant.now();

        TransactionStatistics transactionStatistics = new TransactionStatistics();
        return new ResponseEntity<>(transactionStatistics,HttpStatus.OK);
    }
}
