package com.glidingpath.platform.shared.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.glidingpath.platform.shared.dto.TrusteeConfirmationRequestDTO;
import com.glidingpath.platform.shared.dto.PlanSignatureRequestDTO;
import com.glidingpath.platform.sponsor.dto.CompanyPlanRequestDTO;
import com.glidingpath.core.entity.TrusteeConfirmationEntity;
import com.glidingpath.core.entity.PlanSignatureEntity;
import com.glidingpath.core.entity.TenantPlan;

/**
 * Configuration for ModelMapper bean with auto-mapping enabled.
 * All field names now match between entities and DTOs, so explicit mappings are not needed.
 * Only ID skipping for create operations is maintained.
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Core configuration for optimal auto-mapping
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setAmbiguityIgnored(true);

        // Add ID skipping for CompanyPlanRequestDTO to TenantPlan (create operation)
        mapper.createTypeMap(CompanyPlanRequestDTO.class, TenantPlan.class)
            .addMappings(m -> m.skip(TenantPlan::setId));

        return mapper;
    }
}