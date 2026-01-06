package com.vince.rtd_reliability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class RtdReliabilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtdReliabilityApplication.class, args);
	}

}
