package org.ikigaidigital.domain.interest.strategy;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.interest.InterestStrategy;

public abstract class GracePeriodInterestStrategy implements InterestStrategy {

  private static final int GRACE_PERIOD_DAYS = 30;
  private static final int MONTHS_PER_YEAR = 12;
  private static final double NO_INTEREST = 0;

  @Override
  public final double calculateMonthlyInterest(TimeDeposit deposit) {
    if (deposit.getDays() <= GRACE_PERIOD_DAYS || !isEligibleForInterest(deposit)) {
      return NO_INTEREST;
    }
    return deposit.getBalance() * getAnnualRate() / MONTHS_PER_YEAR;
  }

  protected abstract double getAnnualRate();

  protected boolean isEligibleForInterest(TimeDeposit deposit) {
    return true;
  }
}
