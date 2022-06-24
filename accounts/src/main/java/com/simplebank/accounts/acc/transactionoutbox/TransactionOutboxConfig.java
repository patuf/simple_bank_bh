package com.simplebank.accounts.acc.transactionoutbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Configuration
@Profile("!test")
public class TransactionOutboxConfig {
    private final Log log = LogFactory.getLog(getClass());

    @Value("${account.transactionoutbox.poller.maxresults}")
    private int pollerMaxResults;
    @Value("${account.transactionoutbox.poller.waittimemillis}")
    private long waitTimeMillis;
    @Value("${account.transactionoutbox.poller.kafka.topic}")
    private String kafkaTopic;
    @Value("${account.transactionoutbox.poller.channel}")
    private String pollerChannel;

    @Bean
    public IntegrationFlow pollingAdapterFlow(EntityManagerFactory entityManagerFactory) {
        log.info("Initializing integrationflow for channel " + pollerChannel);
        return IntegrationFlows
                .from(Jpa.inboundAdapter(entityManagerFactory)
                                .entityClass(CreateTransactionCommand.class)
                                .maxResults(pollerMaxResults)
                                .expectSingleResult(false),
                        e -> e.poller(p -> p.trigger(new PeriodicTrigger(waitTimeMillis))))
                .channel(c -> c.queue(pollerChannel))
                .get();
    }

    @Bean
    @Profile({"messages-kafka"})
    @ServiceActivator(inputChannel = "transactionOut")
    public MessageHandler handlerKafka(TransactionOutboxMessageHandlerKafka kafkaPublishingMessageHandler) {
        log.info("Transactional outbox message handler initialised - Kafka profile. Outputting to topic: " + kafkaTopic);
        return kafkaPublishingMessageHandler;
    }

    @Bean
    @Profile({"messages-http"})
    @ServiceActivator(inputChannel = "transactionOut")
    public MessageHandler handlerHttp(TransactionOutboxMessageHandlerHttp httpPublishingMessageHandler) {
        log.info("Transactional outbox message handler initialised - HTTP profile");
        return httpPublishingMessageHandler;
    }
}
