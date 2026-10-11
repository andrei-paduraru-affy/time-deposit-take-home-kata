package org.ikigaidigital.domain.interest.strategy;

import org.ikigaidigital.TimeDeposit;

public class StudentInterestStrategy extends GracePeriodInterestStrategy {

  private static final double ANNUAL_RATE = 0.03;
  private static final int LAST_DAY_WITH_INTEREST = 365;

  @Override
  protected double getAnnualRate() {
    return ANNUAL_RATE;
  }

  @Override
  protected boolean isEligibleForInterest(TimeDeposit deposit) {
    return deposit.getDays() <= LAST_DAY_WITH_INTEREST;
  }
}
