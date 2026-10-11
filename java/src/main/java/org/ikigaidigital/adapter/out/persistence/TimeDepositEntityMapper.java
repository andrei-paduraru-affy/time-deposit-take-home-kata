package org.ikigaidigital.adapter.out.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.springframework.stereotype.Component;

@Component
public class TimeDepositEntityMapper {

  private static final int BALANCE_SCALE = 2;

  public TimeDeposit mapToTimeDeposit(TimeDepositEntity entity) {
    return new TimeDeposit(
        entity.getId(),
        entity.getPlanType().name().toLowerCase(Locale.ROOT),
        entity.getBalance().doubleValue(),
        entity.getDays());
  }

  public BigDecimal mapToBalance(TimeDeposit timeDeposit) {
    return BigDecimal.valueOf(timeDeposit.getBalance())
        .setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
  }
}
