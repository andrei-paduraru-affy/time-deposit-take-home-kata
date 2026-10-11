package org.ikigaidigital.adapter.out.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Locale;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.ikigaidigital.domain.Withdrawal;
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

  public TimeDepositDetails mapToTimeDepositDetails(TimeDepositEntity entity) {
    return new TimeDepositDetails(
        mapToTimeDeposit(entity),
        entity.getWithdrawals().stream()
            .sorted(
                Comparator.comparing(WithdrawalEntity::getDate)
                    .thenComparing(WithdrawalEntity::getId))
            .map(this::mapToWithdrawal)
            .toList());
  }

  public Withdrawal mapToWithdrawal(WithdrawalEntity entity) {
    return new Withdrawal(entity.getId(), entity.getAmount(), entity.getDate());
  }

  public BigDecimal mapToBalance(TimeDeposit timeDeposit) {
    return BigDecimal.valueOf(timeDeposit.getBalance())
        .setScale(BALANCE_SCALE, RoundingMode.HALF_UP);
  }
}
