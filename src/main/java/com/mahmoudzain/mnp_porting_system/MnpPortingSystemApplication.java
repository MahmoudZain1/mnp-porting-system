package com.mahmoudzain.mnp_porting_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class MnpPortingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MnpPortingSystemApplication.class, args);
	}

}
