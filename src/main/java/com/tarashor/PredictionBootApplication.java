package com.tarashor;

import com.tarashor.models.features.BasicFeatures2;
import com.tarashor.models.features.BasicFeatures3;
import com.tarashor.models.features.BasicFeatures5;
import com.tarashor.models.features.Features;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PredictionBootApplication {

    @Bean
    public Features getFeatures(){
        return new BasicFeatures5();
    }

	public static void main(String[] args) {
		SpringApplication.run(PredictionBootApplication.class, args);
	}
}
