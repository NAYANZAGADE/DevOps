package com.glidingpath.auth.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class KeycloakDatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.keycloak")
    public DataSourceProperties keycloakDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "keycloakDataSource")
    public DataSource keycloakDataSource(@Qualifier("keycloakDataSourceProperties") DataSourceProperties properties) {
        // Ensures Hikari gets jdbcUrl from standard 'url' property
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "keycloakJdbcTemplate")
    public JdbcTemplate keycloakJdbcTemplate(@Qualifier("keycloakDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}