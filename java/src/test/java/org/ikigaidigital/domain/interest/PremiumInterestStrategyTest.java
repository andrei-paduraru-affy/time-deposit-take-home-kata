package org.ikigaidigital.domain.interest;

import static org.assertj.core.api.Assertions.assertThat;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.interest.strategy.PremiumInterestStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PremiumInterestStrategyTest {

  private final PremiumInterestStrategy strategy = new PremiumInterestStrategy();

  @ParameterizedTest
  @ValueSource(ints = {0, 30, 31, 45})
  void shouldNotApplyInterestUpTo45Days(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "premium", 1200.00, days)))
        .isZero();
  }

  @ParameterizedTest
  @ValueSource(ints = {46, 365, 1000})
  void shouldApplyOneTwelfthOfFivePercentAfter45Days(int days) {
    assertThat(strategy.calculateMonthlyInterest(new TimeDeposit(1, "premium", 1200.00, days)))
        .isEqualTo(5.00);
  }
}
