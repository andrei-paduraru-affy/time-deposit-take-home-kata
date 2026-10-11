package org.ikigaidigital.domain.interest.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.ikigaidigital.TimeDeposit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GracePeriodInterestStrategyTest {

  private static final double ANNUAL_RATE = 0.12;
  private static final double BALANCE = 1200.00;
  private static final double MONTHLY_INTEREST = 12.00;
  private static final double TOLERANCE = 1e-9;

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 30})
  void shouldNotApplyInterestDuringFirst30Days(int days) {
    var strategy = new FlatRateInterestStrategy();

    assertThat(strategy.calculateMonthlyInterest(depositAged(days))).isZero();
  }

  @ParameterizedTest
  @ValueSource(ints = {31, 100, 1000})
  void shouldApplyOneTwelfthOfTheAnnualRateAfter30Days(int days) {
    var strategy = new FlatRateInterestStrategy();

    assertThat(strategy.calculateMonthlyInterest(depositAged(days)))
        .isCloseTo(MONTHLY_INTEREST, within(TOLERANCE));
  }

  @Test
  void shouldNotApplyInterestToIneligibleDepositEvenAfter30Days() {
    var strategy = new IneligibleInterestStrategy();

    assertThat(strategy.calculateMonthlyInterest(depositAged(100))).isZero();
  }

  private static TimeDeposit depositAged(int days) {
    return new TimeDeposit(1, "flat", BALANCE, days);
  }

  private static class FlatRateInterestStrategy extends GracePeriodInterestStrategy {

    @Override
    protected double getAnnualRate() {
      return ANNUAL_RATE;
    }
  }

  private static class IneligibleInterestStrategy extends FlatRateInterestStrategy {

    @Override
    protected boolean isEligibleForInterest(TimeDeposit deposit) {
      return false;
    }
  }
}
