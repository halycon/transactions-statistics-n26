package com.n26.service;

import java.time.Instant;

public interface StatisticsService<R> {

    R getStatisticsOfaTimePeriod(Instant start, Instant end);
}
