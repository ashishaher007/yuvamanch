package com.ymanch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YuvaManchApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuvaManchApplication.class, args);

		System.out.println("YUVA MANCH SERVER STARTED");
	}

}
