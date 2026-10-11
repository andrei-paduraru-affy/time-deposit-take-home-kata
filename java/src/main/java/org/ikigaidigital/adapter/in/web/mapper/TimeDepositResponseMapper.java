package org.ikigaidigital.adapter.in.web.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.in.web.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.web.model.TimeDepositResponse.PlanTypeEnum;
import org.ikigaidigital.adapter.in.web.model.WithdrawalResponse;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Component;

@Component
public class TimeDepositResponseMapper {

  private static final int MONEY_SCALE = 2;

  public TimeDepositResponse mapToResponse(TimeDepositDetails details) {
    TimeDeposit timeDeposit = details.timeDeposit();
    return new TimeDepositResponse(
        timeDeposit.getId(),
        PlanTypeEnum.fromValue(timeDeposit.getPlanType()),
        toMoney(BigDecimal.valueOf(timeDeposit.getBalance())),
        timeDeposit.getDays(),
        details.withdrawals().stream().map(this::mapToWithdrawalResponse).toList());
  }

  private WithdrawalResponse mapToWithdrawalResponse(Withdrawal withdrawal) {
    return new WithdrawalResponse(withdrawal.id(), toMoney(withdrawal.amount()), withdrawal.date());
  }

  private BigDecimal toMoney(BigDecimal value) {
    return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
  }
}
