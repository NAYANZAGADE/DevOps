package com.glidingpath.finch.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Data;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EmploymentDTO {
    private String individualId;
    private String title;
    private Manager manager;
    private Department department;
    private Employment employment;
    private String startDate;
    private String endDate;
    private String latestRehireDate;
    private Boolean isActive;
    private String employmentStatus;
    private String classCode;
    private Location location;
    private Income income;
    private List<IncomeHistory> incomeHistory;
    private List<CustomField> customFields;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Manager {
        private String id;
        private Boolean isValid;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Department {
        private String name;
        private Boolean isValid;
        private String parent;
        private String sourceId;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Employment {
        private String type;
        private String subtype;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Location {
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private Boolean isValid;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Income {
        private String unit;
        private Integer amount;
        private String currency;
        private String effectiveDate;
        private Boolean isValid;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class IncomeHistory {
        private String unit;
        private Integer amount;
        private String currency;
        private String effectiveDate;
        private Boolean isValid;
    }
    @Data
    public static class CustomField {
        private String name;
        private String value;
    }
} 