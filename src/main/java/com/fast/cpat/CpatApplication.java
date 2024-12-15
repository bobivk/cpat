package com.fast.cpat;

import com.fast.cpat.model.Company;
import com.fast.cpat.repository.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.fast.cpat")
public class CpatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CpatApplication.class, args);

	}
	@Bean
	CommandLineRunner runner(CompanyRepository companyRepository) {
		return args -> {
			Company company = Company.builder().name("new company").metrics(Map.of("Market cap", "$1B")).build();
			companyRepository.save(company);
		};
	}
}
