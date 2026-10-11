package org.ikigaidigital.adapter.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.ikigaidigital.TimeDepositApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TimeDepositApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
class OpenApiContractIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldServeOpenApiContractWithBalanceUpdatesOperation() throws Exception {
    mockMvc
        .perform(get("/openapi/time-deposit-api.yaml"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("title: XA Bank Time Deposit API")))
        .andExpect(content().string(containsString("/api/v1/time-deposits/balance-updates:")))
        .andExpect(content().string(containsString("operationId: updateAllBalances")));
  }

  @Test
  void shouldServeSwaggerUiConfiguredWithTheContract() throws Exception {
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    mockMvc
        .perform(get("/v3/api-docs/swagger-config"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("/openapi/time-deposit-api.yaml")));
  }
}
