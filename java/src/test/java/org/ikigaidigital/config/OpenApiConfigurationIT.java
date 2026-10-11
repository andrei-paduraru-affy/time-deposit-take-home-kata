package org.ikigaidigital.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.ikigaidigital.bootstrap.TimeDepositApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TimeDepositApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class OpenApiConfigurationIT {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldExposeOpenApiDocumentWithApiInfo() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.info.title").value("XA Bank Time Deposit API"))
        .andExpect(jsonPath("$.info.version").value("1.0.0"));
  }

  @Test
  void shouldServeSwaggerUi() throws Exception {
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }
}
