package com.fast.cpat.config;

import com.fast.cpat.service.SecretsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.fast.cpat.repository")
class DataSourceConfig {

    final SecretsService secretsService;

    @Autowired
    public DataSourceConfig(SecretsService secretsService) {
        this.secretsService = secretsService;
    }

    @Primary
    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(this.secretsService.getSecret("db.url"));
        dataSource.setUsername(this.secretsService.getSecret("db.username"));
        dataSource.setPassword(this.secretsService.getSecret("db.password"));

        return dataSource;
    }
}