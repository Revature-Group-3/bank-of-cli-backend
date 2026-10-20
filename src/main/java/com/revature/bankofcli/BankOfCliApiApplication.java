package com.revature.bankofcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BankOfCliApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankOfCliApiApplication.class, args);
	}

}
