package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.ikigaidigital.domain.interest.InterestStrategy;
import org.ikigaidigital.domain.interest.strategy.BasicInterestStrategy;
import org.ikigaidigital.domain.interest.strategy.PremiumInterestStrategy;
import org.ikigaidigital.domain.interest.strategy.StudentInterestStrategy;

public class TimeDepositCalculator {

  private static final int INTEREST_SCALE = 2;
  private static final double NO_INTEREST = 0;

  private final InterestStrategy basicInterestStrategy = new BasicInterestStrategy();
  private final InterestStrategy studentInterestStrategy = new StudentInterestStrategy();
  private final InterestStrategy premiumInterestStrategy = new PremiumInterestStrategy();

  public void updateBalance(List<TimeDeposit> xs) {
    for (TimeDeposit deposit : xs) {
      double interest = calculateMonthlyInterest(deposit);
      double roundedInterest =
          new BigDecimal(interest).setScale(INTEREST_SCALE, RoundingMode.HALF_UP).doubleValue();
      deposit.setBalance(deposit.getBalance() + roundedInterest);
    }
  }

  private double calculateMonthlyInterest(TimeDeposit deposit) {
    return switch (deposit.getPlanType()) {
      case "basic" -> basicInterestStrategy.calculateMonthlyInterest(deposit);
      case "student" -> studentInterestStrategy.calculateMonthlyInterest(deposit);
      case "premium" -> premiumInterestStrategy.calculateMonthlyInterest(deposit);
      default -> NO_INTEREST;
    };
  }
}
