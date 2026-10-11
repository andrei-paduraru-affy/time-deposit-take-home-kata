package org.ikigaidigital.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.ikigaidigital.bootstrap.TimeDepositApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TimeDepositApplication.class)
@ActiveProfiles("local")
@Transactional
class PersistenceEntitiesIT {

  @Autowired private EntityManager entityManager;

  @Test
  void shouldPersistTimeDepositWithGeneratedIdAndPlanTypeStoredAsName() {
    var timeDeposit = new TimeDepositEntity(PlanType.PREMIUM, 60, new BigDecimal("1234567.89"));

    entityManager.persist(timeDeposit);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(TimeDepositEntity.class, timeDeposit.getId());
    assertThat(found.getPlanType()).isEqualTo(PlanType.PREMIUM);
    assertThat(found.getDays()).isEqualTo(60);
    assertThat(found.getBalance()).isEqualByComparingTo("1234567.89");
    assertThat(
            entityManager
                .createNativeQuery("select plan_type from time_deposits where id = :id")
                .setParameter("id", timeDeposit.getId())
                .getSingleResult())
        .isEqualTo("PREMIUM");
  }

  @Test
  void shouldPersistWithdrawalReferencingTimeDeposit() {
    var timeDeposit = new TimeDepositEntity(PlanType.BASIC, 45, new BigDecimal("1000.00"));
    entityManager.persist(timeDeposit);
    var date = LocalDateTime.of(2026, 10, 1, 12, 30);
    var withdrawal = new WithdrawalEntity(timeDeposit.getId(), new BigDecimal("150.25"), date);

    entityManager.persist(withdrawal);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(WithdrawalEntity.class, withdrawal.getId());
    assertThat(found.getTimeDepositId()).isEqualTo(timeDeposit.getId());
    assertThat(found.getAmount()).isEqualByComparingTo("150.25");
    assertThat(found.getDate()).isEqualTo(date);
  }
}
