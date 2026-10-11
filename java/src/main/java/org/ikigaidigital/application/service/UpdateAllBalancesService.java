package org.ikigaidigital.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.application.port.out.LoadTimeDepositsPort;
import org.ikigaidigital.application.port.out.UpdateTimeDepositBalancesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAllBalancesService implements UpdateAllBalancesUseCase {

  private final LoadTimeDepositsPort loadTimeDepositsPort;
  private final UpdateTimeDepositBalancesPort updateTimeDepositBalancesPort;
  private final TimeDepositCalculator timeDepositCalculator;

  @Override
  @Transactional
  public void updateAllBalances() {
    List<TimeDeposit> timeDeposits = loadTimeDepositsPort.loadAllTimeDeposits();
    timeDepositCalculator.updateBalance(timeDeposits);
    updateTimeDepositBalancesPort.updateBalances(timeDeposits);
  }
}
