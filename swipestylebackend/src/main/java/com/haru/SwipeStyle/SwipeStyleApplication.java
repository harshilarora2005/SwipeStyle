package com.haru.SwipeStyle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
public class SwipeStyleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwipeStyleApplication.class, args);
	}

}
