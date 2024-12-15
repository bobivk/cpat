package com.fast.cpat;

import com.fast.cpat.model.Company;
import com.fast.cpat.repository.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class CpatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CpatApplication.class, args);

	}
	@Bean
	CommandLineRunner runner(CompanyRepository companyRepository) {
		return args -> companyRepository.save(Company.builder().name("new company").metrics(Map.of("Market cap", "$1B")).build());
	}
}
