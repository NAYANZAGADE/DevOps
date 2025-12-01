package com.glidingpath.finch.mapping;

import com.tryfinch.api.models.IndividualInDirectory;
import com.glidingpath.finch.dto.DirectoryDTO;

public class FinchSdkToDtoMapper {
    
    /**
     * Converts Finch SDK IndividualInDirectory object to internal DirectoryDTO
     * 
     * <p>This method extracts employee directory information from Finch SDK response
     * and maps it to our internal DirectoryDTO structure. It handles null safety
     * for optional fields provided by Finch API.</p>
     * 
     * @param dir Finch SDK IndividualInDirectory object from API response
     * @return DirectoryDTO internal data transfer object
     * @throws NullPointerException if dir parameter is null
     */
    public static DirectoryDTO toDirectoryDTO(IndividualInDirectory dir) {
        DirectoryDTO dto = new DirectoryDTO();
        dto.setId(dir.id());
        dto.setFirstName(dir.firstName().orElse(null));
        dto.setMiddleName(dir.middleName().orElse(null));
        dto.setLastName(dir.lastName().orElse(null));
        dto.setIsActive(dir.isActive().orElse(null));
        if (dir.manager().isPresent()) {
            DirectoryDTO.DirectoryManager mgr = new DirectoryDTO.DirectoryManager();
            mgr.setId(dir.manager().get().id());
            dto.setManager(mgr);
        }
        if (dir.department().isPresent()) {
            DirectoryDTO.DirectoryDepartment dept = new DirectoryDTO.DirectoryDepartment();
            dept.setName(dir.department().get().name().orElse(null));
            dto.setDepartment(dept);
        }
        return dto;
    }
} 