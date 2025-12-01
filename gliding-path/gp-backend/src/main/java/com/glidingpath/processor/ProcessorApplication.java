package com.glidingpath.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
	scanBasePackages = "com.glidingpath"
)
@EnableJpaRepositories(basePackages = {
	"com.glidingpath.core.repository",
	"com.glidingpath.rules.repository", 
	"com.glidingpath.auth.repository",
	"com.glidingpath.platform.repository",
	"com.glidingpath.common.repository",
	"com.glidingpath.finch.repository"
})
@EntityScan(basePackages = {
	"com.glidingpath.core.entity",
	"com.glidingpath.rules.entity",
	"com.glidingpath.auth.entity", 
	"com.glidingpath.platform.entity",
	"com.glidingpath.common.entity",
	"com.glidingpath.finch.entity"
})
public class ProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessorApplication.class, args);
	}

}
