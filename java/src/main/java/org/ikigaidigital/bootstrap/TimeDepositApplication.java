package org.ikigaidigital.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication(scanBasePackages = "org.ikigaidigital")
@EntityScan("org.ikigaidigital.adapter.out.persistence")
public class TimeDepositApplication {
  public static void main(String[] args) {
    SpringApplication.run(TimeDepositApplication.class, args);
  }
}
