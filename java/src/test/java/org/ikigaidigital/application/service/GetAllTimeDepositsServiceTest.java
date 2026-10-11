package org.ikigaidigital.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;

class GetAllTimeDepositsServiceTest {

  @Test
  void shouldReturnEveryTimeDepositWithItsWithdrawalsAsLoaded() {
    var withdrawal =
        new Withdrawal(5, new BigDecimal("100.00"), LocalDateTime.of(2026, 6, 1, 9, 0));
    var details =
        List.of(
            new TimeDepositDetails(new TimeDeposit(1, "basic", 1000.00, 45), List.of(withdrawal)),
            new TimeDepositDetails(new TimeDeposit(2, "premium", 2000.00, 90), List.of()));
    var service = new GetAllTimeDepositsService(() -> details);

    assertThat(service.getAllTimeDeposits()).containsExactlyElementsOf(details);
  }

  @Test
  void shouldReturnEmptyListWhenThereAreNoTimeDeposits() {
    var service = new GetAllTimeDepositsService(List::of);

    assertThat(service.getAllTimeDeposits()).isEmpty();
  }
}
