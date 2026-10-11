package org.ikigaidigital.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.out.LoadTimeDepositsPort;
import org.ikigaidigital.application.port.out.UpdateTimeDepositBalancesPort;
import org.junit.jupiter.api.Test;

class UpdateAllBalancesServiceTest {

  private final InMemoryTimeDeposits timeDeposits = new InMemoryTimeDeposits();
  private final UpdateAllBalancesService service =
      new UpdateAllBalancesService(timeDeposits, timeDeposits, new TimeDepositCalculator());

  @Test
  void shouldSaveEveryLoadedDepositWithItsInterestApplied() {
    timeDeposits.store(
        List.of(
            new TimeDeposit(1, "basic", 1000.00, 31),
            new TimeDeposit(2, "student", 1000.00, 31),
            new TimeDeposit(3, "premium", 1000.00, 46),
            new TimeDeposit(4, "premium", 1000.00, 10)));

    service.updateAllBalances();

    assertThat(timeDeposits.updated())
        .extracting(TimeDeposit::getId, TimeDeposit::getBalance)
        .containsExactly(
            tuple(1, 1000.83), tuple(2, 1002.50), tuple(3, 1004.17), tuple(4, 1000.00));
  }

  @Test
  void shouldSaveNothingWhenThereAreNoDeposits() {
    service.updateAllBalances();

    assertThat(timeDeposits.updated()).isEmpty();
  }

  private static class InMemoryTimeDeposits
      implements LoadTimeDepositsPort, UpdateTimeDepositBalancesPort {

    private final List<TimeDeposit> stored = new ArrayList<>();
    private final List<TimeDeposit> updated = new ArrayList<>();

    void store(List<TimeDeposit> timeDeposits) {
      stored.addAll(timeDeposits);
    }

    List<TimeDeposit> updated() {
      return updated;
    }

    @Override
    public List<TimeDeposit> loadAllTimeDeposits() {
      return new ArrayList<>(stored);
    }

    @Override
    public void updateBalances(List<TimeDeposit> timeDeposits) {
      updated.addAll(timeDeposits);
    }
  }
}
