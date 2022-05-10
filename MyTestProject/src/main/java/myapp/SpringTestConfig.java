package myapp;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan("myapp")
@Profile("test")
public class SpringTestConfig {
	
	public SpringTestConfig() {
		
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
