package org.ikigaidigital.domain.interest.strategy;

public class BasicInterestStrategy extends GracePeriodInterestStrategy {

  private static final double ANNUAL_RATE = 0.01;

  @Override
  protected double getAnnualRate() {
    return ANNUAL_RATE;
  }
}
