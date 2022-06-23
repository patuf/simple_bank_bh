package com.simplebank.accounts;

import com.simplebank.accounts.customer.CustomerService;
import com.simplebank.accounts.customer.CustomerRepository;
import com.simplebank.accounts.customer.LocalRepoCustomerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableTransactionManagement
public class AccountsApplication {
	private final Log log = LogFactory.getLog(getClass());

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

//	@Bean
//    CustomerService customerDataProvider(CustomerRepository customerRepo) {
//		log.info("Initializing CustomerDataProvider");
//		return new LocalRepoCustomerService(customerRepo);
//	}

	@Bean
	public RestTemplate getRestTemplate() {

		log.info("Initializing RestTemplate");
		return new RestTemplate();
	}
}
