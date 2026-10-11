package org.ikigaidigital.application.port.out;

import java.util.List;
import org.ikigaidigital.TimeDeposit;

public interface LoadTimeDepositsPort {

  List<TimeDeposit> loadAllTimeDeposits();
}
