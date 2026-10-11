package org.ikigaidigital.adapter.in.web;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.ikigaidigital.fixture.PersistenceFixtures.newTimeDeposit;
import static org.ikigaidigital.fixture.PersistenceFixtures.newWithdrawal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.ikigaidigital.AbstractIT;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class TimeDepositControllerV1IT extends AbstractIT {

  private static final String TIME_DEPOSITS_PATH = "/api/v1/time-deposits";
  private static final String BALANCE_UPDATES_PATH = "/api/v1/time-deposits/balance-updates";

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
  void shouldReturnAllTimeDepositsWithTheirWithdrawals() throws Exception {
    var basic = newTimeDeposit(PlanType.BASIC, 45, "2500.00");
    basic
        .getWithdrawals()
        .add(newWithdrawal(basic, "50.50", LocalDateTime.of(2026, 8, 20, 11, 15)));
    basic.getWithdrawals().add(newWithdrawal(basic, "100.00", LocalDateTime.of(2026, 6, 1, 9, 0)));
    var basicId = timeDepositJpaRepository.save(basic).getId();
    var studentId = save(PlanType.STUDENT, 29, "500.00");
    var premiumId = save(PlanType.PREMIUM, 90, "12000.00");

    mockMvc
        .perform(get(TIME_DEPOSITS_PATH))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(basicId))
        .andExpect(jsonPath("$[0].planType").value("basic"))
        .andExpect(jsonPath("$[0].balance").value(2500.00))
        .andExpect(jsonPath("$[0].days").value(45))
        .andExpect(jsonPath("$[0].withdrawals", hasSize(2)))
        .andExpect(jsonPath("$[0].withdrawals[0].amount").value(100.00))
        .andExpect(jsonPath("$[0].withdrawals[0].date").value("2026-06-01T09:00:00"))
        .andExpect(jsonPath("$[0].withdrawals[1].amount").value(50.50))
        .andExpect(jsonPath("$[0].withdrawals[1].date").value("2026-08-20T11:15:00"))
        .andExpect(jsonPath("$[1].id").value(studentId))
        .andExpect(jsonPath("$[1].planType").value("student"))
        .andExpect(jsonPath("$[1].withdrawals", hasSize(0)))
        .andExpect(jsonPath("$[2].id").value(premiumId))
        .andExpect(jsonPath("$[2].planType").value("premium"))
        .andExpect(jsonPath("$[2].balance").value(12000.00))
        .andExpect(jsonPath("$[2].days").value(90));
  }

  @Test
  void shouldReturnEmptyArrayWhenThereAreNoTimeDeposits() throws Exception {
    mockMvc
        .perform(get(TIME_DEPOSITS_PATH))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldReturnUpdatedBalancesAfterBalanceUpdate() throws Exception {
    save(PlanType.BASIC, 31, "1000.00");
    save(PlanType.STUDENT, 399, "3000.00");

    mockMvc.perform(post(BALANCE_UPDATES_PATH)).andExpect(status().isNoContent());

    mockMvc
        .perform(get(TIME_DEPOSITS_PATH))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].balance").value(1000.83))
        .andExpect(jsonPath("$[1].balance").value(3000.00));
  }

  private Map<Integer, BigDecimal> balancesById() {
    return timeDepositJpaRepository.findAll().stream()
        .collect(toMap(TimeDepositEntity::getId, TimeDepositEntity::getBalance));
  }
}
