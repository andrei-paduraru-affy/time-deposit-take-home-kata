package org.ikigaidigital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

  private static final String TITLE = "XA Bank Time Deposit API";
  private static final String DESCRIPTION =
      "Lists time deposits with their withdrawals and applies monthly interest to all balances.";
  private static final String VERSION = "1.0.0";

  @Bean
  OpenAPI timeDepositOpenApi() {
    return new OpenAPI().info(new Info().title(TITLE).description(DESCRIPTION).version(VERSION));
  }
}
