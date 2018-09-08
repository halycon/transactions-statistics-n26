package com.n26.controller;

import com.n26.domain.TransactionStatistics;
import com.n26.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
public class TransactionStatisticsControler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "TransactionStatisticsService")
    private StatisticsService<TransactionStatistics> transactionStatisticsService;

    @RequestMapping(value = "/statistics", method = {RequestMethod.GET} , produces = "application/json")
    private ResponseEntity<TransactionStatistics> statistics(){

        Instant instantOfRequest = Instant.now();

        TransactionStatistics transactionStatistics = transactionStatisticsService.
                getStatisticsOfaTimePeriod(instantOfRequest.minus(1,ChronoUnit.MINUTES),
                        instantOfRequest);

        return new ResponseEntity<>(transactionStatistics,HttpStatus.OK);
    }
}
