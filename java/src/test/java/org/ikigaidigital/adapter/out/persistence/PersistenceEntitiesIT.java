package org.ikigaidigital.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.ikigaidigital.TimeDepositApplication;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TimeDepositApplication.class)
@ActiveProfiles({"local", "test"})
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
    var withdrawal = new WithdrawalEntity(timeDeposit, new BigDecimal("150.25"), date);

    entityManager.persist(withdrawal);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(WithdrawalEntity.class, withdrawal.getId());
    assertThat(found.getTimeDeposit().getId()).isEqualTo(timeDeposit.getId());
    assertThat(found.getAmount()).isEqualByComparingTo("150.25");
    assertThat(found.getDate()).isEqualTo(date);
  }

  @Test
  void shouldLoadAllWithdrawalsOfATimeDeposit() {
    var timeDeposit = new TimeDepositEntity(PlanType.STUDENT, 100, new BigDecimal("5000.00"));
    var otherTimeDeposit = new TimeDepositEntity(PlanType.BASIC, 100, new BigDecimal("700.00"));
    entityManager.persist(timeDeposit);
    entityManager.persist(otherTimeDeposit);
    var date = LocalDateTime.of(2026, 9, 15, 9, 0);
    entityManager.persist(new WithdrawalEntity(timeDeposit, new BigDecimal("100.00"), date));
    entityManager.persist(new WithdrawalEntity(timeDeposit, new BigDecimal("250.50"), date));
    entityManager.persist(new WithdrawalEntity(otherTimeDeposit, new BigDecimal("10.00"), date));
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(TimeDepositEntity.class, timeDeposit.getId());

    assertThat(found.getWithdrawals())
        .extracting(WithdrawalEntity::getAmount)
        .usingElementComparator(BigDecimal::compareTo)
        .containsExactlyInAnyOrder(new BigDecimal("100.00"), new BigDecimal("250.50"));
  }

  @Test
  void shouldPersistWithdrawalsTogetherWithTheirTimeDeposit() {
    var timeDeposit = new TimeDepositEntity(PlanType.PREMIUM, 50, new BigDecimal("3000.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "200.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "300.00"));

    entityManager.persist(timeDeposit);
    entityManager.flush();
    entityManager.clear();

    assertThat(countWithdrawalsOf(timeDeposit)).isEqualTo(2);
  }

  @Test
  void shouldDeleteWithdrawalRemovedFromItsTimeDeposit() {
    var timeDeposit = new TimeDepositEntity(PlanType.BASIC, 40, new BigDecimal("800.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "50.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "75.00"));
    entityManager.persist(timeDeposit);
    entityManager.flush();

    timeDeposit.getWithdrawals().remove(0);
    entityManager.flush();
    entityManager.clear();

    assertThat(countWithdrawalsOf(timeDeposit)).isEqualTo(1);
  }

  @Test
  void shouldDeleteWithdrawalsWhenTheirTimeDepositIsRemoved() {
    var timeDeposit = new TimeDepositEntity(PlanType.STUDENT, 200, new BigDecimal("1500.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "20.00"));
    entityManager.persist(timeDeposit);
    entityManager.flush();

    entityManager.remove(timeDeposit);
    entityManager.flush();
    entityManager.clear();

    assertThat(countWithdrawalsOf(timeDeposit)).isZero();
  }

  @Test
  void shouldDeleteWithdrawalsInTheDatabaseWhenTheirTimeDepositIsBulkDeleted() {
    var timeDeposit = new TimeDepositEntity(PlanType.BASIC, 90, new BigDecimal("900.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "30.00"));
    entityManager.persist(timeDeposit);
    entityManager.flush();
    entityManager.clear();

    entityManager
        .createQuery("DELETE FROM TimeDepositEntity t WHERE t.id = :id")
        .setParameter("id", timeDeposit.getId())
        .executeUpdate();

    assertThat(countWithdrawalsOf(timeDeposit)).isZero();
  }

  private WithdrawalEntity withdrawalOf(TimeDepositEntity timeDeposit, String amount) {
    return new WithdrawalEntity(
        timeDeposit, new BigDecimal(amount), LocalDateTime.of(2026, 10, 1, 10, 0));
  }

  private long countWithdrawalsOf(TimeDepositEntity timeDeposit) {
    return entityManager
        .createQuery(
            "SELECT COUNT(w) FROM WithdrawalEntity w WHERE w.timeDeposit.id = :id", Long.class)
        .setParameter("id", timeDeposit.getId())
        .getSingleResult();
  }
}
