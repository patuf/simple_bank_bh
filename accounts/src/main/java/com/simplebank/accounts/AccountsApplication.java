package com.simplebank.accounts;

import com.gruelbox.transactionoutbox.*;
import com.simplebank.accounts.customer.CustomerDataProvider;
import com.simplebank.accounts.customer.CustomerRepository;
import com.simplebank.accounts.customer.LocalRepoCustomerDataProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@SpringBootApplication
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

	@Bean
	@Lazy
	public TransactionOutbox transactionOutbox(DataSource dataSource
//			SpringTransactionManager springTransactionManager,
//											   SpringInstantiator springInstantiator
	) {
//
		return TransactionOutbox.builder()
//				.instantiator(springInstantiator)
//				.transactionManager(springTransactionManager)
				.transactionManager(TransactionManager.fromDataSource(dataSource))
				.persistor(Persistor.forDialect(Dialect.H2))
				.build();
	}

	@Bean
	CustomerDataProvider customerDataProvider(CustomerRepository customerRepo) {
		return new LocalRepoCustomerDataProvider(customerRepo);
	}
}
