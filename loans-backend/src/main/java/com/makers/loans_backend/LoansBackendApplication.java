package com.makers.loans_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LoansBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoansBackendApplication.class, args);
	}

}
