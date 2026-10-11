package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Withdrawal(Integer id, BigDecimal amount, LocalDateTime date) {}
