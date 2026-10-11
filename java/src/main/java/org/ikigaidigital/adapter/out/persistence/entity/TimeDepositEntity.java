package org.ikigaidigital.adapter.out.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;

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

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal balance;

  @OneToMany(mappedBy = "timeDeposit", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WithdrawalEntity> withdrawals = new ArrayList<>();

  public TimeDepositEntity(PlanType planType, int days, BigDecimal balance) {
    this.planType = planType;
    this.days = days;
    this.balance = balance;
  }
}
