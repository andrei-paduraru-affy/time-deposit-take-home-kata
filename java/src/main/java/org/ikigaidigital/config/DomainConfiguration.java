package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DomainConfiguration {

  @Bean
  TimeDepositCalculator timeDepositCalculator() {
    return new TimeDepositCalculator();
  }
}
