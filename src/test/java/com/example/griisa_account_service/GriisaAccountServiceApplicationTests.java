package com.example.griisa_account_service;

import com.example.griisa_account_service.config.TestJwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(TestJwtConfig.class)
@SuppressWarnings("deprecation")
class GriisaAccountServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
