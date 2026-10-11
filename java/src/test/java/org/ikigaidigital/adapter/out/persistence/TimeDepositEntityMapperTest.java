package org.ikigaidigital.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.adapter.out.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.ikigaidigital.domain.TimeDepositDetails;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimeDepositEntityMapperTest {

  private final TimeDepositEntityMapper mapper = new TimeDepositEntityMapper();

  @ParameterizedTest
  @CsvSource({"BASIC, basic", "STUDENT, student", "PREMIUM, premium"})
  void shouldMapEntityToTimeDepositWithLowercasePlanType(PlanType planType, String expected) {
    var entity = mock(TimeDepositEntity.class);
    when(entity.getId()).thenReturn(7);
    when(entity.getPlanType()).thenReturn(planType);
    when(entity.getBalance()).thenReturn(new BigDecimal("1234.56"));
    when(entity.getDays()).thenReturn(46);

    TimeDeposit timeDeposit = mapper.mapToTimeDeposit(entity);

    assertThat(timeDeposit.getId()).isEqualTo(7);
    assertThat(timeDeposit.getPlanType()).isEqualTo(expected);
    assertThat(timeDeposit.getBalance()).isEqualTo(1234.56);
    assertThat(timeDeposit.getDays()).isEqualTo(46);
  }

  @ParameterizedTest
  @CsvSource({"123.96000000000001, 123.96", "1000.83, 1000.83", "0.005, 0.01", "1000, 1000.00"})
  void shouldMapBalanceToTwoDecimalsRoundingHalfUp(double balance, String expected) {
    var timeDeposit = new TimeDeposit(1, "basic", balance, 31);

    assertThat(mapper.mapToBalance(timeDeposit)).isEqualTo(new BigDecimal(expected));
  }

  @Test
  void shouldMapEntityToDetailsWithWithdrawalsOrderedByDate() {
    var entity = timeDepositEntity();
    var earlier = LocalDateTime.of(2026, 6, 1, 10, 0);
    var later = LocalDateTime.of(2026, 9, 1, 10, 0);
    var laterWithdrawal = withdrawalEntity(2, "20.00", later);
    var earlierWithdrawal = withdrawalEntity(1, "10.00", earlier);
    when(entity.getWithdrawals()).thenReturn(List.of(laterWithdrawal, earlierWithdrawal));

    TimeDepositDetails details = mapper.mapToTimeDepositDetails(entity);

    assertThat(details.timeDeposit().getId()).isEqualTo(7);
    assertThat(details.timeDeposit().getPlanType()).isEqualTo("student");
    assertThat(details.withdrawals())
        .containsExactly(
            new Withdrawal(1, new BigDecimal("10.00"), earlier),
            new Withdrawal(2, new BigDecimal("20.00"), later));
  }

  private TimeDepositEntity timeDepositEntity() {
    var entity = mock(TimeDepositEntity.class);
    when(entity.getId()).thenReturn(7);
    when(entity.getPlanType()).thenReturn(PlanType.STUDENT);
    when(entity.getBalance()).thenReturn(new BigDecimal("500.00"));
    when(entity.getDays()).thenReturn(29);
    return entity;
  }

  private WithdrawalEntity withdrawalEntity(int id, String amount, LocalDateTime date) {
    var entity = mock(WithdrawalEntity.class);
    when(entity.getId()).thenReturn(id);
    when(entity.getAmount()).thenReturn(new BigDecimal(amount));
    when(entity.getDate()).thenReturn(date);
    return entity;
  }
}
