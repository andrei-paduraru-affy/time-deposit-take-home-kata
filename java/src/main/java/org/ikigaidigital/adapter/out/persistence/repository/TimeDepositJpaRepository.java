package org.ikigaidigital.adapter.out.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import org.ikigaidigital.adapter.out.persistence.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

  @EntityGraph(attributePaths = "withdrawals")
  List<TimeDepositEntity> findAllByOrderByIdAsc();

  @Modifying
  @Query("UPDATE TimeDepositEntity t SET t.balance = :balance WHERE t.id = :id")
  void updateBalance(@Param("id") Integer id, @Param("balance") BigDecimal balance);
}
