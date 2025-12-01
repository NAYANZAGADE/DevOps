package com.glidingpath.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "finch_company_details")
public class PlanSponsor extends BaseEntity {
    private String legalName;
    private String ein;
    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "entity_subtype")
    private String entitySubtype;
    private String primaryEmail;
    private String primaryPhoneNumber;
    
    @ElementCollection
    @CollectionTable(name = "finch_company_department", joinColumns = @JoinColumn(name = "company_id"))
    private List<Department> departments;
    
    @ElementCollection
    @CollectionTable(name = "finch_company_location", joinColumns = @JoinColumn(name = "company_id"))
    private List<Location> locations;
    
    @ElementCollection
    @CollectionTable(name = "finch_company_account", joinColumns = @JoinColumn(name = "company_id"))
    private List<Account> accounts;


    @Data
    @Embeddable
    public static class Department {
        private String name;
        private String parent;
    }
    @Data
    @Embeddable
    public static class Location {
        private String city;
        private String country;
        private String line1;
        private String line2;
        private String postalCode;
        private String state;
        private String name;
    }
    @Data
    @Embeddable
    public static class Account {
        private String institutionName;
        private String accountName;
        private String accountType;
        private String accountNumber;
        private String routingNumber;
    }
} 