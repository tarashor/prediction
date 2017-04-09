package com.tarashor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PredictionBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(PredictionBootApplication.class, args);
	}
}
