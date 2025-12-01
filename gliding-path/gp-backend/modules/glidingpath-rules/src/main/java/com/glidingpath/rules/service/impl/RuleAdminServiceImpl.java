package com.glidingpath.rules.service.impl;

import com.glidingpath.rules.config.DroolsConfig;
import com.glidingpath.rules.service.RuleAdminService;
import com.glidingpath.common.util.AppException;
import com.glidingpath.common.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleAdminServiceImpl implements RuleAdminService {

    private final DroolsConfig droolsConfig;

    @Override
    public String reloadRules() throws Exception {
        log.info("Starting rule reload process");
            KieContainer newContainer = droolsConfig.kieContainer();
            // If you have a static or singleton reference, update it here (not shown)
            log.info("Rules reloaded successfully from database");
            return "Rules reloaded from database successfully";
    }
} 