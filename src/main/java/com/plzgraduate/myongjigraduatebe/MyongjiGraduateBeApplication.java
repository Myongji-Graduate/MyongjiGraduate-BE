package com.plzgraduate.myongjigraduatebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MyongjiGraduateBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyongjiGraduateBeApplication.class, args);
	}

}
