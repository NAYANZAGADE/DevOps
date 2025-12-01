package com.glidingpath.finch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class FinchCompanyDetailsDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("legal_name")
    private String legalName;
    @JsonProperty("ein")
    private String ein;
    @JsonProperty("entity")
    private EntityType entity;
    @JsonProperty("primary_email")
    private String primaryEmail;
    @JsonProperty("primary_phone_number")
    private String primaryPhoneNumber;
    @JsonProperty("departments")
    private List<Department> departments;
    @JsonProperty("locations")
    private List<Location> locations;
    @JsonProperty("accounts")
    private List<Account> accounts;

    @Data
    public static class EntityType {
        @JsonProperty("type")
        private String type;
        @JsonProperty("subtype")
        private String subtype;
    }
    @Data
    public static class Department {
        @JsonProperty("name")
        private String name;
        @JsonProperty("parent")
        private String parent;
    }
    @Data
    public static class Location {
        @JsonProperty("city")
        private String city;
        @JsonProperty("country")
        private String country;
        @JsonProperty("line1")
        private String line1;
        @JsonProperty("line2")
        private String line2;
        @JsonProperty("postal_code")
        private String postalCode;
        @JsonProperty("state")
        private String state;
        @JsonProperty("name")
        private String name;
    }
    @Data
    public static class Account {
        @JsonProperty("institution_name")
        private String institutionName;
        @JsonProperty("account_name")
        private String accountName;
        @JsonProperty("account_type")
        private String accountType;
        @JsonProperty("account_number")
        private String accountNumber;
        @JsonProperty("routing_number")
        private String routingNumber;
    }
} 