package com.glidingpath.finch.service.impl;

import com.glidingpath.finch.service.FinchDataFetcherService;
import com.glidingpath.finch.service.impl.FinchClientFactory;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.glidingpath.finch.dto.FinchCompanyDetailsDTO;
import com.glidingpath.core.entity.PlanSponsor;
import com.glidingpath.core.repository.PlanSponsorRepository;
import com.tryfinch.api.client.FinchClient;
import com.tryfinch.api.models.Company;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinchDataFetcherServiceImpl implements FinchDataFetcherService {

    private final FinchClientFactory finchClient;
    private final PlanSponsorRepository companyDetailsRepository;
  
    @Transactional
    public FinchCompanyDetailsDTO syncCompanyData(String tenantId) {
        try {
            FinchClient client = finchClient.createClient(tenantId);
            com.tryfinch.api.models.Company company = client.hris().company().retrieve();

            // Get or create company entity
            PlanSponsor entity = getOrCreateCompany(tenantId);
            
            // Update entity with company data
            updateCompanyEntity(entity, company);
            
            // Save the entity
            companyDetailsRepository.save(entity);

            // Map to DTO and return
            FinchCompanyDetailsDTO dto = mapToDTO(entity, company);
            log.info("Company data sync completed for tenantId: {}", tenantId);
            return dto;
            
        } catch (AppException ae) {
            log.error("AppException during company data sync for tenantId: {}: {}", tenantId, ae.getMessage(), ae);
            throw ae;
        } catch (Exception e) {
            log.error("Unexpected error during company data sync for tenantId: {}: {}", tenantId, e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to sync company data", e);
        }
    }

    private PlanSponsor getOrCreateCompany(String tenantId) {
        Optional<PlanSponsor> existingCompany = companyDetailsRepository.findByTenantId(tenantId);
        if (existingCompany.isPresent()) {
            return existingCompany.get();
        } else {
            PlanSponsor entity = new PlanSponsor();
            entity.setTenantId(tenantId);
            return entity;
        }
    }

    private void updateCompanyEntity(PlanSponsor entity, Company company) {
        // Basic fields
        entity.setLegalName(company.legalName().orElse(null));
        entity.setEin(company.ein().orElse(null));
        entity.setPrimaryEmail(company.primaryEmail().orElse(null));
        entity.setPrimaryPhoneNumber(company.primaryPhoneNumber().orElse(null));

        // Entity type/subtype
        if (company.entity().isPresent()) {
            var entityObj = company.entity().get();
            entity.setEntityType(entityObj.type().isPresent() ? entityObj.type().get().toString() : null);
            entity.setEntitySubtype(entityObj.subtype().isPresent() ? entityObj.subtype().get().toString() : null);
        }
        
        // Collections
        entity.setDepartments(mapDepartments(company));
        entity.setLocations(mapLocations(company));
        entity.setAccounts(mapAccounts(company));
    }

    private List<PlanSponsor.Department> mapDepartments(Company company) {
        if (!company.departments().isPresent()) {
            return new ArrayList<>();
        }
        
        List<PlanSponsor.Department> departments = new ArrayList<>();
        for (var d : company.departments().get()) {
            PlanSponsor.Department dept = new PlanSponsor.Department();
            dept.setName(d.name().orElse(null));
            dept.setParent(d.parent().isPresent() ? d.parent().get().toString() : null);
            departments.add(dept);
        }
        return departments;
    }

    private List<PlanSponsor.Location> mapLocations(Company company) {
        if (!company.locations().isPresent()) {
            return new ArrayList<>();
        }
        
        List<PlanSponsor.Location> locations = new ArrayList<>();
        for (var l : company.locations().get()) {
            PlanSponsor.Location loc = new PlanSponsor.Location();
            loc.setCity(l.city().orElse(null));
            loc.setCountry(l.country().orElse(null));
            loc.setLine1(l.line1().orElse(null));
            loc.setLine2(l.line2().orElse(null));
            loc.setPostalCode(l.postalCode().orElse(null));
            loc.setState(l.state().orElse(null));
            loc.setName(l.name().orElse(null));
            locations.add(loc);
        }
        return locations;
    }

    private List<PlanSponsor.Account> mapAccounts(Company company) {
        if (!company.accounts().isPresent()) {
            return new ArrayList<>();
        }
        
        List<PlanSponsor.Account> accounts = new ArrayList<>();
        for (var a : company.accounts().get()) {
            PlanSponsor.Account acc = new PlanSponsor.Account();
            acc.setInstitutionName(a.institutionName().orElse(null));
            acc.setAccountName(a.accountName().orElse(null));
            acc.setAccountType(a.accountType().isPresent() ? a.accountType().get().toString() : null);
            acc.setAccountNumber(a.accountNumber().orElse(null));
            acc.setRoutingNumber(a.routingNumber().orElse(null));
            accounts.add(acc);
        }
        return accounts;
    }

    private FinchCompanyDetailsDTO mapToDTO(PlanSponsor entity, Company company) {
        FinchCompanyDetailsDTO dto = new FinchCompanyDetailsDTO();
        dto.setId(company.id() != null ? company.id() : entity.getTenantId());
        dto.setLegalName(entity.getLegalName());
        dto.setEin(entity.getEin());
        dto.setPrimaryEmail(entity.getPrimaryEmail());
        dto.setPrimaryPhoneNumber(entity.getPrimaryPhoneNumber());
        
        // Entity type
        if (entity.getEntityType() != null || entity.getEntitySubtype() != null) {
            FinchCompanyDetailsDTO.EntityType entityType = new FinchCompanyDetailsDTO.EntityType();
            entityType.setType(entity.getEntityType());
            entityType.setSubtype(entity.getEntitySubtype());
            dto.setEntity(entityType);
        }
        
        // Collections
        dto.setDepartments(mapDepartmentsToDTO(entity.getDepartments()));
        dto.setLocations(mapLocationsToDTO(entity.getLocations()));
        dto.setAccounts(mapAccountsToDTO(entity.getAccounts()));
        
        return dto;
    }

    private List<FinchCompanyDetailsDTO.Department> mapDepartmentsToDTO(List<PlanSponsor.Department> departments) {
        if (departments == null) return null;
        return departments.stream().map(d -> {
            FinchCompanyDetailsDTO.Department dept = new FinchCompanyDetailsDTO.Department();
            dept.setName(d.getName());
            dept.setParent(d.getParent());
            return dept;
        }).toList();
    }

    private List<FinchCompanyDetailsDTO.Location> mapLocationsToDTO(List<PlanSponsor.Location> locations) {
        if (locations == null) return null;
        return locations.stream().map(l -> {
            FinchCompanyDetailsDTO.Location loc = new FinchCompanyDetailsDTO.Location();
            loc.setCity(l.getCity());
            loc.setCountry(l.getCountry());
            loc.setLine1(l.getLine1());
            loc.setLine2(l.getLine2());
            loc.setPostalCode(l.getPostalCode());
            loc.setState(l.getState());
            loc.setName(l.getName());
            return loc;
        }).toList();
    }

    private List<FinchCompanyDetailsDTO.Account> mapAccountsToDTO(List<PlanSponsor.Account> accounts) {
        if (accounts == null) return null;
        return accounts.stream().map(a -> {
            FinchCompanyDetailsDTO.Account acc = new FinchCompanyDetailsDTO.Account();
            acc.setInstitutionName(a.getInstitutionName());
            acc.setAccountName(a.getAccountName());
            acc.setAccountType(a.getAccountType());
            acc.setAccountNumber(a.getAccountNumber());
            acc.setRoutingNumber(a.getRoutingNumber());
            return acc;
        }).toList();
    }
    
    @Override
    public void getAllBenefits(String tenantId) throws Exception {
        log.info("Getting all benefits for tenantId: {}", tenantId);
        // TODO: Implement benefit fetching logic
        // This is a placeholder implementation
        throw new UnsupportedOperationException("Benefit fetching not yet implemented");
    }
}