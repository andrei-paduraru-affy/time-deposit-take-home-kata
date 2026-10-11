package org.ikigaidigital;

import static org.ikigaidigital.fixture.PersistenceFixtures.newTimeDeposit;

import jakarta.persistence.EntityManager;
import org.ikigaidigital.adapter.out.persistence.entity.enums.PlanType;
import org.ikigaidigital.adapter.out.persistence.repository.TimeDepositJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    classes = TimeDepositApplication.class,
    properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public abstract class AbstractIT {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected EntityManager entityManager;
  @Autowired protected TimeDepositJpaRepository timeDepositJpaRepository;

  @BeforeEach
  void clearTimeDeposits() {
    timeDepositJpaRepository.deleteAllInBatch();
  }

  protected Integer save(PlanType planType, int days, String balance) {
    return timeDepositJpaRepository.save(newTimeDeposit(planType, days, balance)).getId();
  }
}
