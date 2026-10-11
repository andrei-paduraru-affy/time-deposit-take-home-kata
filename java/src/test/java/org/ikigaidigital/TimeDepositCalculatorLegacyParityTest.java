package org.ikigaidigital;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimeDepositCalculatorLegacyParityTest {

  private static final List<String> PLAN_TYPES =
      List.of("basic", "student", "premium", "unknown", "Basic", "");
  private static final double[] BALANCES = {
    0.0, 0.01, 0.99, 100.0, 123.45, 150.0, 999.99, 1000.0, 1200.0, 25000.0, 1234567.0, 99999999.99
  };
  private static final int MAX_DAYS = 1500;
  private static final int MONTHS = 12;

  private final TimeDepositCalculator calculator = new TimeDepositCalculator();

  @Test
  void shouldProduceExactlyTheSameBalancesAsTheLegacyImplementationForEveryPlanAndDay() {
    List<String> differences = new ArrayList<>();

    for (String planType : PLAN_TYPES) {
      for (int days = 0; days <= MAX_DAYS; days++) {
        for (double balance : BALANCES) {
          var deposit = new TimeDeposit(1, planType, balance, days);
          double legacyBalance = balance;
          for (int month = 0; month < MONTHS; month++) {
            calculator.updateBalance(List.of(deposit));
            legacyBalance = legacyBalanceAfterOneMonth(planType, days, legacyBalance);
          }
          if (Double.compare(deposit.getBalance(), legacyBalance) != 0) {
            differences.add(
                "%s, %d days, balance %s: expected %s but was %s"
                    .formatted(planType, days, balance, legacyBalance, deposit.getBalance()));
          }
        }
      }
    }

    assertThat(differences).isEmpty();
  }

  private static double legacyBalanceAfterOneMonth(String planType, int days, double balance) {
    double interest = 0;

    if (days > 30) {
      if (planType.equals("student")) {
        if (days < 366) {
          interest += balance * 0.03 / 12;
        }
      } else if (planType.equals("premium")) {
        if (days > 45) {
          interest += balance * 0.05 / 12;
        }
      } else if (planType.equals("basic")) {
        interest += balance * 0.01 / 12;
      }
    }

    return balance + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
  }
}
