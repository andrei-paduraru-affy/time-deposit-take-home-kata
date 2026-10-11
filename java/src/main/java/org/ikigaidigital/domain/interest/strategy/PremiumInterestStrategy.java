package org.ikigaidigital.domain.interest.strategy;

import org.ikigaidigital.TimeDeposit;

public class PremiumInterestStrategy extends GracePeriodInterestStrategy {

  private static final double ANNUAL_RATE = 0.05;
  private static final int LAST_DAY_WITHOUT_INTEREST = 45;

  @Override
  protected double getAnnualRate() {
    return ANNUAL_RATE;
  }

  @Override
  protected boolean isEligibleForInterest(TimeDeposit deposit) {
    return deposit.getDays() > LAST_DAY_WITHOUT_INTEREST;
  }
}
