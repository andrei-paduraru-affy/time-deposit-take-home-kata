package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "time_deposits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeDepositEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlanType planType;

  @Column(nullable = false)
  private int days;

  @Setter
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  public TimeDepositEntity(PlanType planType, int days, BigDecimal balance) {
    this.planType = planType;
    this.days = days;
    this.balance = balance;
  }
}
