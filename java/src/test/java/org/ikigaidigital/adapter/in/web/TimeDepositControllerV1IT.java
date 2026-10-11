package org.ikigaidigital.adapter.in.web;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;
import org.ikigaidigital.TimeDepositApplication;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.ikigaidigital.adapter.out.persistence.repository.TimeDepositJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = TimeDepositApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TimeDepositControllerV1IT {

  private static final String BALANCE_UPDATES_PATH = "/api/v1/time-deposits/balance-updates";

  @Autowired private MockMvc mockMvc;
  @Autowired private TimeDepositJpaRepository timeDepositJpaRepository;

  @BeforeEach
  void setUp() {
    timeDepositJpaRepository.deleteAllInBatch();
  }

  @Test
  void shouldApplyMonthlyInterestToEveryTimeDepositAndReturnNoContent() throws Exception {
    var basic = save(PlanType.BASIC, 31, "1000.00");
    var student = save(PlanType.STUDENT, 365, "1000.00");
    var studentAfterOneYear = save(PlanType.STUDENT, 366, "1000.00");
    var premium = save(PlanType.PREMIUM, 46, "1000.00");
    var premiumTooEarly = save(PlanType.PREMIUM, 45, "1000.00");
    var gracePeriod = save(PlanType.BASIC, 30, "1000.00");

    mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isNoContent());

    assertThat(balancesById())
        .containsEntry(basic, new BigDecimal("1000.83"))
        .containsEntry(student, new BigDecimal("1002.50"))
        .containsEntry(studentAfterOneYear, new BigDecimal("1000.00"))
        .containsEntry(premium, new BigDecimal("1004.17"))
        .containsEntry(premiumTooEarly, new BigDecimal("1000.00"))
        .containsEntry(gracePeriod, new BigDecimal("1000.00"));
  }

  @Test
  void shouldApplyInterestAgainOnEveryCall() throws Exception {
    var basic = save(PlanType.BASIC, 31, "1000.00");

    mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isNoContent());
    mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isNoContent());

    assertThat(balancesById()).containsEntry(basic, new BigDecimal("1001.66"));
  }

  @Test
  void shouldReturnNoContentWhenThereAreNoTimeDeposits() throws Exception {
    mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isNoContent());
  }

  private Integer save(PlanType planType, int days, String balance) {
    return timeDepositJpaRepository
        .save(new TimeDepositEntity(planType, days, new BigDecimal(balance)))
        .getId();
  }

  private Map<Integer, BigDecimal> balancesById() {
    return timeDepositJpaRepository.findAll().stream()
        .collect(toMap(TimeDepositEntity::getId, TimeDepositEntity::getBalance));
  }
}
