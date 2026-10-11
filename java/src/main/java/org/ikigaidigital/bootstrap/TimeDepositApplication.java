package org.ikigaidigital.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.ikigaidigital")
public class TimeDepositApplication {
  public static void main(String[] args) {
    SpringApplication.run(TimeDepositApplication.class, args);
  }
}
