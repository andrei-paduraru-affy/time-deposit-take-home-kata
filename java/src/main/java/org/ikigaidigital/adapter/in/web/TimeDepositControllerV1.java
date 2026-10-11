package org.ikigaidigital.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.adapter.in.web.api.TimeDepositsApi;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TimeDepositControllerV1 implements TimeDepositsApi {

  private final UpdateAllBalancesUseCase updateAllBalancesUseCase;

  @Override
  public ResponseEntity<Void> updateAllBalances() {
    updateAllBalancesUseCase.updateAllBalances();
    return ResponseEntity.noContent().build();
  }
}
