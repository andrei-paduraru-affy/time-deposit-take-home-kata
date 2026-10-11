package org.ikigaidigital.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;

public final class PersistenceFixtures {

  private PersistenceFixtures() {}

  public static TimeDepositEntity newTimeDeposit(PlanType planType, int days, String balance) {
    return new TimeDepositEntity(planType, days, new BigDecimal(balance));
  }

  public static WithdrawalEntity newWithdrawal(
      TimeDepositEntity timeDeposit, String amount, LocalDateTime date) {
    return new WithdrawalEntity(timeDeposit, new BigDecimal(amount), date);
  }
}
