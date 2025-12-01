package com.glidingpath.finch.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DirectoryDTO {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private Boolean isActive;
    private DirectoryManager manager;
    private DirectoryDepartment department;

    @Data
    public static class DirectoryManager {
        private String id;
    }
    @Data
    public static class DirectoryDepartment {
        private String name;
    }
} 