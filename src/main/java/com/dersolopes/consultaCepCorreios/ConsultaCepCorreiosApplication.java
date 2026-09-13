package com.dersolopes.consultaCepCorreios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConsultaCepCorreiosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsultaCepCorreiosApplication.class, args);
	}

}
