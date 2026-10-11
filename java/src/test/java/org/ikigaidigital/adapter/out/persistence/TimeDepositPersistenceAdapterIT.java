package org.ikigaidigital.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositApplication;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = TimeDepositApplication.class)
@ActiveProfiles("local")
@Transactional
class TimeDepositPersistenceAdapterIT {

  @Autowired private TimeDepositPersistenceAdapter adapter;
  @Autowired private EntityManager entityManager;

  private TimeDepositEntity basic;
  private TimeDepositEntity premium;

  @BeforeEach
  void setUp() {
    entityManager.createQuery("DELETE FROM WithdrawalEntity").executeUpdate();
    entityManager.createQuery("DELETE FROM TimeDepositEntity").executeUpdate();
    basic = new TimeDepositEntity(PlanType.BASIC, 31, new BigDecimal("1000.00"));
    premium = new TimeDepositEntity(PlanType.PREMIUM, 46, new BigDecimal("2500.50"));
    entityManager.persist(basic);
    entityManager.persist(premium);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void shouldLoadAllTimeDepositsAsDomainObjects() {
    List<TimeDeposit> timeDeposits = adapter.loadAllTimeDeposits();

    assertThat(timeDeposits)
        .extracting(
            TimeDeposit::getId,
            TimeDeposit::getPlanType,
            TimeDeposit::getBalance,
            TimeDeposit::getDays)
        .containsExactlyInAnyOrder(
            tuple(basic.getId(), "basic", 1000.00, 31),
            tuple(premium.getId(), "premium", 2500.50, 46));
  }

  @Test
  void shouldUpdateOnlyTheBalanceOfEachTimeDeposit() {
    adapter.updateBalances(
        List.of(
            new TimeDeposit(basic.getId(), "basic", 1000.83, 31),
            new TimeDeposit(premium.getId(), "premium", 123.96000000000001, 46)));
    entityManager.clear();

    var updatedBasic = entityManager.find(TimeDepositEntity.class, basic.getId());
    var updatedPremium = entityManager.find(TimeDepositEntity.class, premium.getId());
    assertThat(updatedBasic.getBalance()).isEqualByComparingTo("1000.83");
    assertThat(updatedBasic.getPlanType()).isEqualTo(PlanType.BASIC);
    assertThat(updatedBasic.getDays()).isEqualTo(31);
    assertThat(updatedPremium.getBalance()).isEqualByComparingTo("123.96");
  }
}
