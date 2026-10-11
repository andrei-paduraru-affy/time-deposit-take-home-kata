package org.ikigaidigital.domain;

import java.util.List;
import org.ikigaidigital.TimeDeposit;

public record TimeDepositDetails(TimeDeposit timeDeposit, List<Withdrawal> withdrawals) {}
