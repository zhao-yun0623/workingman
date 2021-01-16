package com.workingman;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.workingman.mapper")
public class WorkingmanApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkingmanApplication.class, args);
	}

}
