package org.ikigaidigital.domain.interest;

import static org.assertj.core.api.Assertions.assertThat;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.interest.strategy.StudentInterestStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StudentInterestStrategyTest {

  private final StudentInterestStrategy strategy = new StudentInterestStrategy();

  @ParameterizedTest
  @ValueSource(ints = {0, 30})
  void shouldNotApplyInterestDuringFirst30Days(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "student", 1200.00, days)))
        .isZero();
  }

  @ParameterizedTest
  @ValueSource(ints = {31, 365})
  void shouldApplyOneTwelfthOfThreePercentFrom31To365Days(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "student", 1200.00, days)))
        .isEqualTo(3.00);
  }

  @ParameterizedTest
  @ValueSource(ints = {366, 1000})
  void shouldNotApplyInterestAfterOneYear(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "student", 1200.00, days)))
        .isZero();
  }
}
