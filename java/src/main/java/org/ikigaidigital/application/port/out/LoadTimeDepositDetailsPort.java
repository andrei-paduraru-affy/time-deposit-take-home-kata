package org.ikigaidigital.application.port.out;

import java.util.List;
import org.ikigaidigital.domain.TimeDepositDetails;

public interface LoadTimeDepositDetailsPort {

  List<TimeDepositDetails> loadAllTimeDepositDetails();
}
