package org.ikigaidigital.adapter.out.persistence.repository;

import java.math.BigDecimal;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

  @Modifying
  @Query("UPDATE TimeDepositEntity t SET t.balance = :balance WHERE t.id = :id")
  void updateBalance(@Param("id") Integer id, @Param("balance") BigDecimal balance);
}
