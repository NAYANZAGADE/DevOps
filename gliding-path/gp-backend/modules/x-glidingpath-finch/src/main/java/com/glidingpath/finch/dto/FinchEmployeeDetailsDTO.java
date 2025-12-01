package com.glidingpath.finch.dto;

import lombok.Data;
import java.util.List;

@Data
public class FinchEmployeeDetailsDTO {
    private String individualId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private List<Email> emails;
    private List<PhoneNumber> phoneNumbers;
    private String gender;
    private String ethnicity;
    private String dob;
    private Manager manager;
    private Department department;
    private Boolean isActive;
    private Employment employment;
    private String title;
    private String employmentStatus;
    private String startDate;
    private String endDate;
    private String latestRehireDate;
    private String classCode;
    private Address location;
    private Address residence;
    private Income income;
    private List<IncomeHistory> incomeHistory;
    private List<CustomField> customFields;

    @Data
    public static class Email {
        private String data;
        private String type;
    }
    @Data
    public static class PhoneNumber {
        private String data;
        private String type;
    }
    @Data
    public static class Manager {
        private String id;
    }
    @Data
    public static class Department {
        private String name;
    }
    @Data
    public static class Employment {
        private String type;
        private String subtype;
    }
    @Data
    public static class Address {
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }
    @Data
    public static class Income {
        private String unit;
        private Long amount;
        private String currency;
        private String effectiveDate;
    }
    @Data
    public static class IncomeHistory {
        private String unit;
        private Long amount;
        private String currency;
        private String effectiveDate;
    }
    @Data
    public static class CustomField {
        private String name;
        private String value;
    }
} 