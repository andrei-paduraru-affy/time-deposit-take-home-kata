package org.ikigaidigital.domain.interest;

import static org.assertj.core.api.Assertions.assertThat;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.interest.strategy.BasicInterestStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BasicInterestStrategyTest {

  private final BasicInterestStrategy strategy = new BasicInterestStrategy();

  @ParameterizedTest
  @ValueSource(ints = {0, 30})
  void shouldNotApplyInterestDuringFirst30Days(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "basic", 1200.00, days)))
        .isZero();
  }

  @ParameterizedTest
  @ValueSource(ints = {31, 365, 1000})
  void shouldApplyOneTwelfthOfOnePercentAfter30Days(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "basic", 1200.00, days)))
        .isEqualTo(1.00);
  }
}
