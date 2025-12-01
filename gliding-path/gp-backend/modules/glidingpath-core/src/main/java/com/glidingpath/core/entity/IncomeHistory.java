package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "finch_employee_income_history")
@Data
public class IncomeHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id")
    private UUID employeeId;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "amount")
    private Long amount;
    
    @Column(name = "currency")
    private String currency;
    
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
}
