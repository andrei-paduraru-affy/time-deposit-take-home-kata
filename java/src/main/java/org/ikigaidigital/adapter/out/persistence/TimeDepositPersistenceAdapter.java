package org.ikigaidigital.adapter.out.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.out.persistence.repository.TimeDepositJpaRepository;
import org.ikigaidigital.application.port.out.LoadTimeDepositsPort;
import org.ikigaidigital.application.port.out.UpdateTimeDepositBalancesPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeDepositPersistenceAdapter
    implements LoadTimeDepositsPort, UpdateTimeDepositBalancesPort {

  private final TimeDepositJpaRepository timeDepositJpaRepository;
  private final TimeDepositEntityMapper timeDepositEntityMapper;

  @Override
  public List<TimeDeposit> loadAllTimeDeposits() {
    return timeDepositJpaRepository.findAll().stream()
        .map(timeDepositEntityMapper::mapToTimeDeposit)
        .toList();
  }

  @Override
  public void updateBalances(List<TimeDeposit> timeDeposits) {
    timeDeposits.forEach(
        timeDeposit ->
            timeDepositJpaRepository.updateBalance(
                timeDeposit.getId(), timeDepositEntityMapper.mapToBalance(timeDeposit)));
  }
}
