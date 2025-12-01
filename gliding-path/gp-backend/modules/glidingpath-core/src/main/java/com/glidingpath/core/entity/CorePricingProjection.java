package com.glidingpath.core.entity;

import java.math.BigDecimal;

/**
 * JPA Projection interface to fetch only required fields for CorePricing.
 */
public interface CorePricingProjection {
    BigDecimal getBaseFee();
    BigDecimal getParticipantFee();
    BigDecimal getEmployerAccountFee();
    BigDecimal getEmployeeAccountFee();
}

