package com.simplebank.accounts;

import com.simplebank.accounts.acc.transactionoutbox.CreateTransactionCommandOutbox;
import com.simplebank.accounts.acc.transactionoutbox.TransactionOutboxRepository;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.customer.CustomerRepository;
import com.simplebank.accounts.customer.LocalRepoCustomerDataProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@SpringBootApplication
@EnableTransactionManagement
public class AccountsApplication {
	private final Log log = LogFactory.getLog(getClass());

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

	@Bean
	public IntegrationFlow pollingAdapterFlow(EntityManagerFactory entityManagerFactory) {
		return IntegrationFlows
				.from(Jpa.inboundAdapter(entityManagerFactory)
								.entityClass(CreateTransactionCommandOutbox.class)
								.maxResults(1)
								.expectSingleResult(true),
						e -> e.poller(p -> p.trigger(new PeriodicTrigger(1000))))
				.channel(c -> c.queue("transactionOut"))
				.get();
	}

	@Bean
	@ServiceActivator(inputChannel = "transactionOut")
	public MessageHandler handler(TransactionOutboxRepository troutRepo) {
//		troutRepo
		return message -> {
			log.info("MESSAGE RECEIVED!!!");
			troutRepo.delete((CreateTransactionCommandOutbox) message.getPayload());
			log.info("MESSAGE DELETED!!!");
		};
	}

	@Bean
	CustomerDataProvider customerDataProvider(CustomerRepository customerRepo) {
		return new LocalRepoCustomerDataProvider(customerRepo);
	}
}
