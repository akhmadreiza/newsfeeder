package com.ara27.newsfeeder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class NewsfeederApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsfeederApplication.class, args);
	}

}
