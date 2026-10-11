package org.ikigaidigital.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.in.web.mapper.TimeDepositResponseMapper;
import org.ikigaidigital.adapter.in.web.model.TimeDepositResponse;
import org.ikigaidigital.adapter.in.web.model.TimeDepositResponse.PlanTypeEnum;
import org.ikigaidigital.adapter.in.web.model.WithdrawalResponse;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;

class TimeDepositResponseMapperTest {

  private final TimeDepositResponseMapper mapper = new TimeDepositResponseMapper();

  @Test
  void shouldMapTimeDepositAndWithdrawalsToResponse() {
    var date = LocalDateTime.of(2026, 8, 30, 15, 10);
    var details =
        new TimeDepositDetails(
            new TimeDeposit(3, "premium", 12000.0, 90),
            List.of(new Withdrawal(7, new BigDecimal("250.5"), date)));

    TimeDepositResponse response = mapper.mapToResponse(details);

    assertThat(response.getId()).isEqualTo(3);
    assertThat(response.getPlanType()).isEqualTo(PlanTypeEnum.PREMIUM);
    assertThat(response.getBalance()).isEqualTo(new BigDecimal("12000.00"));
    assertThat(response.getDays()).isEqualTo(90);
    assertThat(response.getWithdrawals())
        .containsExactly(new WithdrawalResponse(7, new BigDecimal("250.50"), date));
  }

  @Test
  void shouldExposeBalanceWithTwoDecimalsWithoutDoubleArtifacts() {
    var details =
        new TimeDepositDetails(new TimeDeposit(1, "premium", 123.96000000000001, 46), List.of());

    assertThat(mapper.mapToResponse(details).getBalance()).isEqualTo(new BigDecimal("123.96"));
  }
}
