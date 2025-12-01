package com.glidingpath.platform.sponsor.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.glidingpath.core.entity.SystemConfiguration;
import com.glidingpath.core.repository.SystemConfigurationRepository;
import com.glidingpath.platform.sponsor.service.ConfigService;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SystemConfigurationRepository configRepo;

    @Override
    public JsonNode getCommonConfig() {
            return configRepo.findByConfigKeyAndIsActiveTrue("common_config")
                .map(SystemConfiguration::getConfigValue)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Common configuration not found"));
    }
} 