package com.example.sell_lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SellLpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SellLpApplication.class, args);
	}
}
