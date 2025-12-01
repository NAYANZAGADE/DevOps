package com.glidingpath.finch.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Data;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IndividualDTO {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
    private List<Email> emails;
    private List<PhoneNumber> phoneNumbers;
    private String gender;
    private String ethnicity;
    private String dob;
    private Residence residence;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Email {
        private String data;
        private String type;
        private Boolean isValid;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PhoneNumber {
        private String data;
        private String type;
        private Boolean isValid;
    }
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Residence {
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private Boolean isValid;
    }
} 