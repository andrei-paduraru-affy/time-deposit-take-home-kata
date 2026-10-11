package org.ikigaidigital.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Configuration(proxyBeanMethods = false)
@Profile("local")
public class LocalPostgresContainerConfiguration {

  private static final String POSTGRES_IMAGE = "postgres:17-alpine";
  private static final String DATABASE_NAME = "timedeposits";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "postgres";

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgresContainer() {
    return new PostgreSQLContainer(POSTGRES_IMAGE)
        .withDatabaseName(DATABASE_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD);
  }
}
