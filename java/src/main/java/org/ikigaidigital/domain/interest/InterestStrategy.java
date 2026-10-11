package org.ikigaidigital.domain.interest;

import org.ikigaidigital.TimeDeposit;

public interface InterestStrategy {

  double calculateMonthlyInterest(TimeDeposit deposit);
}
