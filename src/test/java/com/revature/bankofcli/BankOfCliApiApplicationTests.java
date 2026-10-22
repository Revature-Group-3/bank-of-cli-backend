package com.revature.bankofcli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

// Starts the whole application, which needs a real database and JWT secret.
// Only runs when DB_URL is set as an environment variable, so CI (which has no database)
// skips it and still runs every unit test.
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class BankOfCliApiApplicationTests {

	@Test
	void contextLoads() {
	}

}