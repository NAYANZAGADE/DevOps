package com.glidingpath.finch.mapping;

import com.glidingpath.finch.constants.FinchConstants;
import com.glidingpath.finch.dto.DirectoryDTO;
import com.glidingpath.finch.dto.EmploymentDTO;
import com.glidingpath.finch.dto.IndividualDTO;
import com.glidingpath.finch.dto.FinchEmployeeDetailsDTO;
import com.glidingpath.core.entity.PlanParticipant;
import com.glidingpath.core.entity.IncomeHistory;
import com.glidingpath.finch.util.FinchMappingUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Slf4j
public class FinchMapping {
    
    /**
     * Converts multiple DTOs from different Finch APIs into a single employee entity
     * 
     * @param directory DirectoryDTO from Finch directory API
     * @param individual IndividualDTO from Finch individual API
     * @param employment EmploymentDTO from Finch employment API
     * @param tenantId Tenant identifier for the employee
     * @return PlanParticipant entity ready for database persistence
     * @throws IllegalArgumentException if required parameters are null
     */
    public static PlanParticipant toEntity(
        DirectoryDTO directory,
        IndividualDTO individual,
        EmploymentDTO employment,
        String tenantId
    ) {
        PlanParticipant entity = new PlanParticipant();
        entity.setTenantId(tenantId);
        entity.setIndividualId(individual.getId());
        
        // Map basic individual fields
        mapBasicIndividualFields(entity, individual);
        
        // Map collections
        entity.setEmails(mapEmails(individual.getEmails()));
        entity.setPhoneNumbers(mapPhoneNumbers(individual.getPhoneNumbers()));
        
        // Map embedded objects
        mapManagerFromDirectory(entity, directory);
        mapDepartmentFromDirectory(entity, directory);
        mapResidenceAddress(entity, individual);
        
        entity.setIsActive(directory.getIsActive());
        
        // Map employment data
        if (employment != null) {
            mapEmploymentData(entity, employment);
        }
        
        return entity;
    }
    
    /**
     * Maps basic individual fields from IndividualDTO to entity
     * 
     * @param entity Target PlanParticipant entity
     * @param individual Source IndividualDTO with personal information
     */
    private static void mapBasicIndividualFields(PlanParticipant entity, IndividualDTO individual) {
        entity.setFirstName(individual.getFirstName());
        entity.setMiddleName(individual.getMiddleName());
        entity.setLastName(individual.getLastName());
        entity.setPreferredName(individual.getPreferredName());
        entity.setGender(individual.getGender());
        entity.setEthnicity(individual.getEthnicity());
        entity.setDob(FinchMappingUtil.parseDate(individual.getDob()));
    }
    
    /**
     * Maps email collection from IndividualDTO to entity
     * 
     * @param emails List of emails from IndividualDTO
     * @return List of PlanParticipant.Email objects
     */
    private static java.util.List<String> mapEmails(java.util.List<IndividualDTO.Email> emails) {
        if (emails == null || emails.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return FinchMappingUtil.mapList(emails, e -> e.getData());
    }
    
    /**
     * Maps phone number collection from IndividualDTO to entity
     * 
     * @param phoneNumbers List of phone numbers from IndividualDTO
     * @return List of PlanParticipant.PhoneNumber objects
     */
    private static java.util.List<String> mapPhoneNumbers(java.util.List<IndividualDTO.PhoneNumber> phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return FinchMappingUtil.mapList(phoneNumbers, p -> p.getData());
    }
    
    /**
     * Maps manager information from DirectoryDTO to entity
     * 
     * @param entity Target PlanParticipant entity
     * @param directory Source DirectoryDTO with manager information
     */
    private static void mapManagerFromDirectory(PlanParticipant entity, DirectoryDTO directory) {
        if (directory != null && directory.getManager() != null) {
            entity.setManagerId(directory.getManager().getId());
        }
    }
    
    /**
     * Maps department information from DirectoryDTO to entity
     * 
     * @param entity Target PlanParticipant entity
     * @param directory Source DirectoryDTO with department information
     */
    private static void mapDepartmentFromDirectory(PlanParticipant entity, DirectoryDTO directory) {
        if (directory != null && directory.getDepartment() != null) {
            entity.setDepartmentName(directory.getDepartment().getName());
        }
    }
    
    private static void mapResidenceAddress(PlanParticipant entity, IndividualDTO individual) {
        if (individual != null && individual.getResidence() != null) {
            IndividualDTO.Residence residence = individual.getResidence();
            entity.setResidenceLine1(residence.getLine1());
            entity.setResidenceLine2(residence.getLine2());
            entity.setResidenceCity(residence.getCity());
            entity.setResidenceState(residence.getState());
            entity.setResidencePostalCode(residence.getPostalCode());
            entity.setResidenceCountry(residence.getCountry());
        }
    }
    
    private static void mapEmploymentData(PlanParticipant entity, EmploymentDTO employment) {
        if (employment == null) {
            return;
        }
        
        // Basic employment fields
        entity.setTitle(employment.getTitle());
        entity.setEmploymentStatus(employment.getEmploymentStatus());
        entity.setStartDate(FinchMappingUtil.parseDate(employment.getStartDate()));
        entity.setEndDate(FinchMappingUtil.parseDate(employment.getEndDate()));
        entity.setLatestRehireDate(FinchMappingUtil.parseDate(employment.getLatestRehireDate()));
        entity.setIsActive(employment.getIsActive());
        entity.setClassCode(employment.getClassCode());
        
        // Override manager and department from employment if present
        if (employment.getManager() != null) {
            entity.setManagerId(employment.getManager().getId());
        }
        
        if (employment.getDepartment() != null) {
            entity.setDepartmentName(employment.getDepartment().getName());
        }
        
        // Map employment type/subtype
        if (employment.getEmployment() != null) {
            entity.setEmploymentType(employment.getEmployment().getType());
            entity.setEmploymentSubtype(employment.getEmployment().getSubtype());
        }
        
        // Map location address
        if (employment.getLocation() != null) {
            EmploymentDTO.Location location = employment.getLocation();
            entity.setLocationLine1(location.getLine1());
            entity.setLocationLine2(location.getLine2());
            entity.setLocationCity(location.getCity());
            entity.setLocationState(location.getState());
            entity.setLocationPostalCode(location.getPostalCode());
            entity.setLocationCountry(location.getCountry());
        }
        
        // Map income
        if (employment.getIncome() != null) {
            entity.setIncomeUnit(employment.getIncome().getUnit());
            entity.setIncomeAmount(FinchMappingUtil.toLong(employment.getIncome().getAmount()));
            entity.setIncomeCurrency(employment.getIncome().getCurrency());
            entity.setIncomeEffectiveDate(employment.getIncome().getEffectiveDate());
        }
        
        // Map income history
        entity.setIncomeHistory(mapIncomeHistory(employment.getIncomeHistory()));
        
        // Map custom fields
        entity.setCustomFields(mapCustomFields(employment.getCustomFields()));
    }
    
    private static List<IncomeHistory> mapIncomeHistory(java.util.List<EmploymentDTO.IncomeHistory> incomeHistory) {
        if (incomeHistory == null || incomeHistory.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<IncomeHistory> incomeHistoryList = new ArrayList<>();
        for (EmploymentDTO.IncomeHistory ih : incomeHistory) {
            IncomeHistory history = new IncomeHistory();
            history.setUnit(ih.getUnit());
            history.setAmount(ih.getAmount() != null ? ih.getAmount().longValue() : null);
            history.setCurrency(ih.getCurrency());
            history.setEffectiveDate(FinchMappingUtil.parseDate(ih.getEffectiveDate()));
            incomeHistoryList.add(history);
        }
        return incomeHistoryList;
    }
    
    private static Map<String, String> mapCustomFields(java.util.List<EmploymentDTO.CustomField> customFields) {
        if (customFields == null || customFields.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        Map<String, String> customFieldsMap = new java.util.HashMap<>();
        for (EmploymentDTO.CustomField cf : customFields) {
            if (cf.getName() != null && cf.getValue() != null) {
                customFieldsMap.put(cf.getName(), cf.getValue());
            }
        }
        return customFieldsMap;
    }
    


    public static FinchEmployeeDetailsDTO toDTO(PlanParticipant entity) {
        if (entity == null) {
            return null;
        }
        
        FinchEmployeeDetailsDTO dto = new FinchEmployeeDetailsDTO();
        
        // Map basic fields
        mapBasicFieldsToDTO(entity, dto);
        
        // Map collections
        dto.setEmails(mapEmailsToDTO(entity.getEmails()));
        dto.setPhoneNumbers(mapPhoneNumbersToDTO(entity.getPhoneNumbers()));
        dto.setIncomeHistory(mapIncomeHistoryToDTO(entity.getIncomeHistory()));
        dto.setCustomFields(mapCustomFieldsToDTO(entity.getCustomFields()));
        
        // Map embedded objects
        mapManagerToDTO(entity, dto);
        mapDepartmentToDTO(entity, dto);
        mapEmploymentToDTO(entity, dto);
        mapAddressesToDTO(entity, dto);
        mapIncomeToDTO(entity, dto);
        
        return dto;
    }
    
    private static void mapBasicFieldsToDTO(PlanParticipant entity, FinchEmployeeDetailsDTO dto) {
        dto.setIndividualId(entity.getIndividualId());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setPreferredName(entity.getPreferredName());
        dto.setGender(entity.getGender());
        dto.setEthnicity(entity.getEthnicity());
        dto.setDob(entity.getDob() != null ? entity.getDob().format(FinchConstants.DATE_FORMATTER) : null);
        dto.setIsActive(entity.getIsActive());
    }
    
    private static java.util.List<FinchEmployeeDetailsDTO.Email> mapEmailsToDTO(java.util.List<String> emails) {
        if (emails == null || emails.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return FinchMappingUtil.mapList(emails, e -> {
            FinchEmployeeDetailsDTO.Email email = new FinchEmployeeDetailsDTO.Email();
            email.setData(e);
            email.setType("primary"); // Default type since we only store the data
            return email;
        });
    }
    
    private static java.util.List<FinchEmployeeDetailsDTO.PhoneNumber> mapPhoneNumbersToDTO(java.util.List<String> phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return FinchMappingUtil.mapList(phoneNumbers, p -> {
            FinchEmployeeDetailsDTO.PhoneNumber phone = new FinchEmployeeDetailsDTO.PhoneNumber();
            phone.setData(p);
            phone.setType("mobile"); // Default type since we only store the data
            return phone;
        });
    }
    
    private static java.util.List<FinchEmployeeDetailsDTO.IncomeHistory> mapIncomeHistoryToDTO(List<IncomeHistory> incomeHistory) {
        if (incomeHistory == null || incomeHistory.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return FinchMappingUtil.mapList(incomeHistory, ih -> {
            FinchEmployeeDetailsDTO.IncomeHistory history = new FinchEmployeeDetailsDTO.IncomeHistory();
            history.setUnit(ih.getUnit());
            history.setAmount(ih.getAmount());
            history.setCurrency(ih.getCurrency());
            history.setEffectiveDate(ih.getEffectiveDate() != null ? ih.getEffectiveDate().toString() : null);
            return history;
        });
    }
    
    private static java.util.List<FinchEmployeeDetailsDTO.CustomField> mapCustomFieldsToDTO(Map<String, String> customFields) {
        if (customFields == null || customFields.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        return customFields.entrySet().stream()
            .map(entry -> {
                FinchEmployeeDetailsDTO.CustomField field = new FinchEmployeeDetailsDTO.CustomField();
                field.setName(entry.getKey());
                field.setValue(entry.getValue());
                return field;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    private static void mapManagerToDTO(PlanParticipant entity, FinchEmployeeDetailsDTO dto) {
        if (entity.getManagerId() != null) {
            FinchEmployeeDetailsDTO.Manager mgr = new FinchEmployeeDetailsDTO.Manager();
            mgr.setId(entity.getManagerId());
            dto.setManager(mgr);
        }
    }
    
    private static void mapDepartmentToDTO(PlanParticipant entity, FinchEmployeeDetailsDTO dto) {
        if (entity.getDepartmentName() != null) {
            FinchEmployeeDetailsDTO.Department dept = new FinchEmployeeDetailsDTO.Department();
            dept.setName(entity.getDepartmentName());
            dto.setDepartment(dept);
        }
    }
    
    private static void mapEmploymentToDTO(PlanParticipant entity, FinchEmployeeDetailsDTO dto) {
        dto.setTitle(entity.getTitle());
        dto.setEmploymentStatus(entity.getEmploymentStatus());
        dto.setStartDate(entity.getStartDate() != null ? entity.getStartDate().format(FinchConstants.DATE_FORMATTER) : null);
        dto.setEndDate(entity.getEndDate() != null ? entity.getEndDate().format(FinchConstants.DATE_FORMATTER) : null);
        dto.setLatestRehireDate(entity.getLatestRehireDate() != null ? entity.getLatestRehireDate().format(FinchConstants.DATE_FORMATTER) : null);
        dto.setClassCode(entity.getClassCode());
        
        if (entity.getEmploymentType() != null || entity.getEmploymentSubtype() != null) {
            FinchEmployeeDetailsDTO.Employment emp = new FinchEmployeeDetailsDTO.Employment();
            emp.setType(entity.getEmploymentType());
            emp.setSubtype(entity.getEmploymentSubtype());
            dto.setEmployment(emp);
        }
    }
    
    private static void mapAddressesToDTO(PlanParticipant entity, FinchEmployeeDetailsDTO dto) {
        // Map residence address
        if (entity.getResidenceLine1() != null || entity.getResidenceCity() != null) {
            FinchEmployeeDetailsDTO.Address residence = new FinchEmployeeDetailsDTO.Address();
            residence.setLine1(entity.getResidenceLine1());
            residence.setLine2(entity.getResidenceLine2());
            residence.setCity(entity.getResidenceCity());
            residence.setState(entity.getResidenceState());
            residence.setPostalCode(entity.getResidencePostalCode());
            residence.setCountry(entity.getResidenceCountry());
            dto.setResidence(residence);
        }
        
        // Map location address
        if (entity.getLocationLine1() != null || entity.getLocationCity() != null) {
            FinchEmployeeDetailsDTO.Address location = new FinchEmployeeDetailsDTO.Address();
            location.setLine1(entity.getLocationLine1());
            location.setLine2(entity.getLocationLine2());
            location.setCity(entity.getLocationCity());
            location.setState(entity.getLocationState());
            location.setPostalCode(entity.getLocationPostalCode());
            location.setCountry(entity.getLocationCountry());
            dto.setLocation(location);
        }
    }
    
    private static void mapIncomeToDTO(PlanParticipant entity, FinchEmployeeDetailsDTO dto) {
        if (entity.getIncomeUnit() != null || entity.getIncomeAmount() != null) {
            FinchEmployeeDetailsDTO.Income income = new FinchEmployeeDetailsDTO.Income();
            income.setUnit(entity.getIncomeUnit());
            income.setAmount(entity.getIncomeAmount());
            income.setCurrency(entity.getIncomeCurrency());
            income.setEffectiveDate(entity.getIncomeEffectiveDate());
            dto.setIncome(income);
        }
    }
    

} 