package org.ikigaidigital.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.ikigaidigital.fixture.PersistenceFixtures.newTimeDeposit;
import static org.ikigaidigital.fixture.PersistenceFixtures.newWithdrawal;

import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.ikigaidigital.AbstractIT;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class TimeDepositPersistenceAdapterIT extends AbstractIT {

  @Autowired private TimeDepositPersistenceAdapter adapter;
  @Autowired private EntityManagerFactory entityManagerFactory;

  private TimeDepositEntity basic;
  private TimeDepositEntity premium;

  @BeforeEach
  void setUp() {
    basic = newTimeDeposit(PlanType.BASIC, 31, "1000.00");
    premium = newTimeDeposit(PlanType.PREMIUM, 46, "2500.50");
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

  @Test
  void shouldLoadTimeDepositDetailsWithWithdrawalsOrderedByDate() {
    var june = LocalDateTime.of(2026, 6, 1, 9, 0);
    var august = LocalDateTime.of(2026, 8, 20, 11, 15);
    var firstId = persistWithdrawal(basic, "50.00", august);
    var secondId = persistWithdrawal(basic, "100.00", june);

    List<TimeDepositDetails> details = adapter.loadAllTimeDepositDetails();

    assertThat(details)
        .extracting(detail -> detail.timeDeposit().getId())
        .containsExactly(basic.getId(), premium.getId());
    assertThat(details.get(0).withdrawals())
        .containsExactly(
            new Withdrawal(secondId, new BigDecimal("100.00"), june),
            new Withdrawal(firstId, new BigDecimal("50.00"), august));
    assertThat(details.get(1).withdrawals()).isEmpty();
  }

  @Test
  void shouldLoadAllTimeDepositsAndWithdrawalsInASingleQuery() {
    var date = LocalDateTime.of(2026, 7, 15, 14, 30);
    persistWithdrawal(basic, "10.00", date);
    persistWithdrawal(basic, "20.00", date);
    persistWithdrawal(premium, "30.00", date);
    Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    statistics.clear();

    adapter.loadAllTimeDepositDetails();

    assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
  }

  private Integer persistWithdrawal(TimeDepositEntity owner, String amount, LocalDateTime date) {
    var reference = entityManager.getReference(TimeDepositEntity.class, owner.getId());
    var withdrawal = newWithdrawal(reference, amount, date);
    entityManager.persist(withdrawal);
    entityManager.flush();
    entityManager.clear();
    return withdrawal.getId();
  }
}
