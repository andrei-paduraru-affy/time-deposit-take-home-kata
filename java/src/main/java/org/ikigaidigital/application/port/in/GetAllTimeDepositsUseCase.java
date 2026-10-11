package org.ikigaidigital.application.port.in;

import java.util.List;
import org.ikigaidigital.domain.TimeDepositDetails;

public interface GetAllTimeDepositsUseCase {

  List<TimeDepositDetails> getAllTimeDeposits();
}
