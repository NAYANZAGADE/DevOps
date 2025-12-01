package com.glidingpath.core.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "finch_employee_details")
public class PlanParticipant extends BaseEntity {
    
    @Column(name = "individual_id", unique = true, nullable = false)
    private String individualId;
    
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String gender;
    private String ethnicity;
    private LocalDate dob;
    private Boolean isActive;
    private String title;
    private String employmentStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate latestRehireDate;
    private String classCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private PlanSponsor company;

    // Simple column mappings instead of embedded objects
    @Column(name = "manager_id")
    private String managerId;
    
    @Column(name = "department_name")
    private String departmentName;
    
    @Column(name = "employment_type")
    private String employmentType;
    
    @Column(name = "employment_subtype")
    private String employmentSubtype;
    
    @Column(name = "location_line1")
    private String locationLine1;
    
    @Column(name = "location_line2")
    private String locationLine2;
    
    @Column(name = "location_city")
    private String locationCity;
    
    @Column(name = "location_state")
    private String locationState;
    
    @Column(name = "location_postal_code")
    private String locationPostalCode;
    
    @Column(name = "location_country")
    private String locationCountry;
    
    @Column(name = "residence_line1")
    private String residenceLine1;
    
    @Column(name = "residence_line2")
    private String residenceLine2;
    
    @Column(name = "residence_city")
    private String residenceCity;
    
    @Column(name = "residence_state")
    private String residenceState;
    
    @Column(name = "residence_postal_code")
    private String residencePostalCode;
    
    @Column(name = "residence_country")
    private String residenceCountry;
    
    @Column(name = "income_unit")
    private String incomeUnit;
    
    @Column(name = "income_amount")
    private Long incomeAmount;
    
    @Column(name = "income_currency")
    private String incomeCurrency;
    
    @Column(name = "income_effective_date")
    private String incomeEffectiveDate;

    // Element collections - simplified
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "finch_employee_email", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "data")
    private List<String> emails;
    
    @ElementCollection
    @CollectionTable(name = "finch_employee_phone_number", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "data")
    private List<String> phoneNumbers;
    
    @OneToMany(mappedBy = "employeeId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IncomeHistory> incomeHistory;
    
    @ElementCollection
    @CollectionTable(name = "finch_employee_custom_field", joinColumns = @JoinColumn(name = "employee_id"))
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> customFields;

    // Eligibility tracking fields (protected from Finch webhooks)
    @Column(name = "is_eligible_for_401k")
    private Boolean isEligibleFor401k = false;
    
    @Column(name = "eligibility_date")
    private LocalDate eligibilityDate;
    
    @Column(name = "last_eligibility_check")
    private LocalDate lastEligibilityCheck;
    
    @Column(name = "eligibility_reason")
    private String eligibilityReason; // Why eligible/not eligible
    
    @Column(name = "eligibility_status")
    private String eligibilityStatus; // PENDING, ELIGIBLE, NOT_ELIGIBLE, SUSPENDED
    
    @Column(name = "next_eligibility_check_date")
    private LocalDate nextEligibilityCheckDate;
    
    @Column(name = "eligibility_notes")
    private String eligibilityNotes; // Additional notes for eligibility decisions

    // Helper methods to maintain compatibility
    public String getManagerId() {
        return managerId;
    }
    
    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public String getEmploymentType() {
        return employmentType;
    }
    
    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }
    
    public String getEmploymentSubtype() {
        return employmentSubtype;
    }
    
    public void setEmploymentSubtype(String employmentSubtype) {
        this.employmentSubtype = employmentSubtype;
    }
    
    @Override
    public String toString() {
        return "PlanParticipant{" +
               "individualId='" + individualId + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", employmentStatus='" + employmentStatus + '\'' +
               ", employmentType='" + employmentType + '\'' +
               ", startDate=" + startDate +
               ", isEligibleFor401k=" + isEligibleFor401k +
               ", eligibilityStatus='" + eligibilityStatus + '\'' +
               '}';
    }
} 