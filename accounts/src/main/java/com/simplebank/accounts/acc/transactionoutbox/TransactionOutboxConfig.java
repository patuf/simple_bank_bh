package com.simplebank.accounts.acc.transactionoutbox;

import com.simplebank.accounts.acc.AccountService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Configuration
public class TransactionOutboxConfig {
    @Value("${account.transactionoutbox.poller.maxresults}")
    private int pollerMaxResults;
    @Value("${account.transactionoutbox.poller.waittimemillis}")
    private long waitTimeMillis;
    @Value("${account.transactionoutbox.poller.kafka.topic}")
    private String kafkaTopic;

    private final Log log = LogFactory.getLog(getClass());
    @Bean
    public IntegrationFlow pollingAdapterFlow(EntityManagerFactory entityManagerFactory) {
        return IntegrationFlows
                .from(Jpa.inboundAdapter(entityManagerFactory)
                                .entityClass(CreateTransactionCommand.class)
                                .maxResults(pollerMaxResults)
                                .expectSingleResult(false),
                        e -> e.poller(p -> p.trigger(new PeriodicTrigger(waitTimeMillis))))
                .channel(c -> c.queue("transactionOut"))
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "transactionOut")
    public MessageHandler handler(CreateTransactionCommandRepository troutRepo, KafkaTemplate kafkaTmpl) {
        return message -> {
            List<CreateTransactionCommand> payloads = (List<CreateTransactionCommand>) message.getPayload();

            for (CreateTransactionCommand payload: payloads) {
                kafkaTmpl.send(kafkaTopic, payload);
                troutRepo.delete(payload);
                log.info("Message processed: New account transaction for customerId: " + payload.getCustomerId());
            }
        };
    }
}
