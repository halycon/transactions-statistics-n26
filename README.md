## transactions-statistics-n26
Assignment project for N26 company.

RESTFul API to calculate realtime statistics for the last 60 seconds of transactions.

## Operations

##### POST /transactions

This endpoint is called to create a new transaction.

Body:
```
{
  "amount": "12.3343",
  "timestamp": "2018-07-17T09:59:51.312Z"
}
```
Where:

amount – transaction amount; a string of arbitrary length that is parsable as a BigDecimal
timestamp – transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ in the UTC timezone (this is not the current timestamp)
 

Returns: Empty body with one of the following:

201 – in case of success
204 – if the transaction is older than 60 seconds
400 – if the JSON is invalid
422 – if any of the fields are not parsable or the transaction date is in the future
 

##### GET /statistics

This endpoint returns the statistics based on the transactions that happened in the last 60 seconds.

Returns:
```
{
  "sum": "1000.00",
  "avg": "100.53",
  "max": "200000.49",
  "min": "50.23",
  "count": 10
}
```
Where:

sum – a BigDecimal specifying the total sum of transaction value in the last 60 seconds

avg – a BigDecimal specifying the average amount of transaction value in the last 60 seconds

max – a BigDecimal specifying single highest transaction value in the last 60 seconds

min – a BigDecimal specifying single lowest transaction value in the last 60 seconds

count – a long specifying the total number of transactions that happened in the last 60 seconds

##### DELETE /transactions

This endpoint causes all existing transactions to be deleted

The endpoint should accept an empty request body and return a 204 status code.

## Tech/Framework used
Spring Boot

<b>Built with</b>
- [Maven](https://maven.apache.org/)

## Installation

Maven have to be installed on local

"maven test" for testing

"mvn clean integration-test" for integration tests

"mvn spring-boot:run" for getting service up 

      
© [Volkan Cetin]()