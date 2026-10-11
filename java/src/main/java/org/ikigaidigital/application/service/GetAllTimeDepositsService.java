package org.ikigaidigital.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.out.LoadTimeDepositDetailsPort;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllTimeDepositsService implements GetAllTimeDepositsUseCase {

  private final LoadTimeDepositDetailsPort loadTimeDepositDetailsPort;

  @Override
  @Transactional(readOnly = true)
  public List<TimeDepositDetails> getAllTimeDeposits() {
    return loadTimeDepositDetailsPort.loadAllTimeDepositDetails();
  }
}
