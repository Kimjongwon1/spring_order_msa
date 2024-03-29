package com.encore.ordering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MemberOrderingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberOrderingApplication.class, args);
	}

}
