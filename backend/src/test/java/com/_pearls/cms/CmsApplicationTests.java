package com._pearls.cms;

import com._pearls.cms.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CmsApplicationTests {

	@Autowired
	private SecurityConfig securityConfig; // Inject any core configuration bean

	@Test
	void contextLoads() {
		assertThat(securityConfig).isNotNull();
	}

}
