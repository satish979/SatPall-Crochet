package com.satpall.crochet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LoomelleCrochetApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoomelleCrochetApplication.class, args);
	}

}
