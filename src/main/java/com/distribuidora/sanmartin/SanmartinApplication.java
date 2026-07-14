package com.distribuidora.sanmartin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SanmartinApplication {

	public static void main(String[] args) {
		SpringApplication.run(SanmartinApplication.class, args);
	}

}