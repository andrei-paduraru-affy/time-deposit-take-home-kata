package org.ikigaidigital.adapter.in.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.adapter.in.web.api.TimeDepositsApi;
import org.ikigaidigital.adapter.in.web.mapper.TimeDepositResponseMapper;
import org.ikigaidigital.adapter.in.web.model.TimeDepositResponse;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimeDepositControllerV1 implements TimeDepositsApi {

  private final UpdateAllBalancesUseCase updateAllBalancesUseCase;
  private final GetAllTimeDepositsUseCase getAllTimeDepositsUseCase;
  private final TimeDepositResponseMapper timeDepositResponseMapper;

  @Override
  public ResponseEntity<List<TimeDepositResponse>> getAllTimeDeposits() {
    return ResponseEntity.ok(
        getAllTimeDepositsUseCase.getAllTimeDeposits().stream()
            .map(timeDepositResponseMapper::mapToResponse)
            .toList());
  }

  @Override
  public ResponseEntity<Void> updateAllBalances() {
    updateAllBalancesUseCase.updateAllBalances();
    return ResponseEntity.noContent().build();
  }
}
