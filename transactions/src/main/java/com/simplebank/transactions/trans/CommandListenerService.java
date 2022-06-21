package com.simplebank.transactions.trans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CommandListenerService {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired BankTransactionRepository btRepo;

    @Bean
    public RecordMessageConverter converter() {
        return new JsonMessageConverter();
    }

    @KafkaListener(topics = "#{'${bank.transactions.kafka.topic}'}")
    public void subscribe(BankTransaction bankTransaction) throws IOException {
        log.debug(String.format("Consumed Create Message command for amount -> %s", bankTransaction.getAmount()));
        // Ignore command's ID, and insert in the table with freshly generated ID
        bankTransaction.setId(null);
        btRepo.save(bankTransaction);
    }
}
