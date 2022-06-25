package com.simplebank.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

/**
 * The SpringBootApplication for the BankTransactions service.
 */
@SpringBootApplication
public class TransactionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionsApplication.class, args);
	}

}
