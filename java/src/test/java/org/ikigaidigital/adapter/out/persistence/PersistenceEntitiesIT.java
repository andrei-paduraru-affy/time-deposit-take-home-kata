package org.ikigaidigital.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ikigaidigital.fixture.PersistenceFixtures.newTimeDeposit;
import static org.ikigaidigital.fixture.PersistenceFixtures.newWithdrawal;

import java.time.LocalDateTime;
import org.ikigaidigital.AbstractIT;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PersistenceEntitiesIT extends AbstractIT {

  @Test
  void shouldPersistTimeDepositWithGeneratedIdAndPlanTypeStoredAsName() {
    var timeDeposit = newTimeDeposit(PlanType.PREMIUM, 60, "1234567.89");

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
    var timeDeposit = newTimeDeposit(PlanType.BASIC, 45, "1000.00");
    entityManager.persist(timeDeposit);
    var date = LocalDateTime.of(2026, 10, 1, 12, 30);
    var withdrawal = newWithdrawal(timeDeposit, "150.25", date);

    entityManager.persist(withdrawal);
    entityManager.flush();
    entityManager.clear();

    var found = entityManager.find(WithdrawalEntity.class, withdrawal.getId());
    assertThat(found.getTimeDeposit().getId()).isEqualTo(timeDeposit.getId());
    assertThat(found.getAmount()).isEqualByComparingTo("150.25");
    assertThat(found.getDate()).isEqualTo(date);
  }

  @Test
  void shouldPersistWithdrawalsTogetherWithTheirTimeDeposit() {
    var timeDeposit = newTimeDeposit(PlanType.PREMIUM, 50, "3000.00");
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "200.00"));
    timeDeposit.getWithdrawals().add(withdrawalOf(timeDeposit, "300.00"));

    entityManager.persist(timeDeposit);
    entityManager.flush();
    entityManager.clear();

    assertThat(countWithdrawalsOf(timeDeposit)).isEqualTo(2);
  }

  @Test
  void shouldDeleteWithdrawalRemovedFromItsTimeDeposit() {
    var timeDeposit = newTimeDeposit(PlanType.BASIC, 40, "800.00");
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
    var timeDeposit = newTimeDeposit(PlanType.STUDENT, 200, "1500.00");
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
    var timeDeposit = newTimeDeposit(PlanType.BASIC, 90, "900.00");
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
    return newWithdrawal(timeDeposit, amount, LocalDateTime.of(2026, 10, 1, 10, 0));
  }

  private long countWithdrawalsOf(TimeDepositEntity timeDeposit) {
    return entityManager
        .createQuery(
            "SELECT COUNT(w) FROM WithdrawalEntity w WHERE w.timeDeposit.id = :id", Long.class)
        .setParameter("id", timeDeposit.getId())
        .getSingleResult();
  }
}
