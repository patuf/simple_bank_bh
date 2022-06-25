package com.simplebank.accounts.acc.transactionoutbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * This class acts as the Polling Publisher for the Transaction outbox that sends commands to
 * the BankTransactions service. This particular implementation uses a KafkaTemplate to send
 * asynchronous commands to a KafkaServer.
 */
@Service
public class TransactionOutboxMessageHandlerKafka implements MessageHandler {
    private final Log log = LogFactory.getLog(getClass());

    @Value("${account.transactionoutbox.poller.kafka.topic}")
    private String kafkaTopic;
    @Autowired
    private CreateTransactionCommandRepository troutRepo;
    @Autowired
    private KafkaTemplate<Long, CreateTransactionCommand> kafkaTmpl;

    @Override
    @Transactional
    public void handleMessage(Message<?> message) throws MessagingException {
        List<CreateTransactionCommand> payloads = (List<CreateTransactionCommand>) message.getPayload();

        for (CreateTransactionCommand payload : payloads) {
            try {
                kafkaTmpl.send(kafkaTopic, payload);
                log.debug("Message sent to Kafka queue: New account transaction for customerId: " + payload.getCustomerId());
            } catch (Throwable ex) {
                log.error(String.format("CreateTransaction command for accountId %d was not sent due to failure! Retries anb DLQ are not implemented - sorry!", payload.getAccountId()), ex);
            }
            troutRepo.delete(payload);
        }
    }
}
