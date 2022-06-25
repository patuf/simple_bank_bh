package com.simplebank.transactions.trans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * The Kafka listener providing the CRUD services for SimpleBank's BankTransactions service.
 * This listener is initialized only when a "messages-kafka" SpringBoot profile is set, i.e. the asynchronous
 * communication between the Accounts and BankTransactions service is done through Kafka topics.
 * Separating the reporting controller from the CRUD controller facilitates easier implementation of the CQRS pattern.
 */
@Service
@Profile({"messages-kafka"})
public class BankTransactionKafkaController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired BankTransactionRepository btRepo;

    @Bean
    public RecordMessageConverter converter() {
        return new JsonMessageConverter();
    }

    public BankTransactionKafkaController() {
        log.info("BankTransactionHttpController initialized");
    }

    /**
     * Kafka listener endpoint. Registers a new BankTransaction as per the provided requestBody.
     * In an ideal implementation, this method should send a response to a preconfigured response topic.
     * @param bankTransaction The transaction to be registered
     */
    @KafkaListener(topics = "#{'${bank.transactions.kafka.topic}'}")
    public void receiveCreateTransactionCommand(BankTransaction bankTransaction) throws IOException {
        log.debug(String.format("Consumed Create Message Kafka command for amount -> %s", bankTransaction.getAmount()));
        // Ignore command's ID, and insert in the table with freshly generated ID
        bankTransaction.setId(null);
        btRepo.save(bankTransaction);
    }
}
