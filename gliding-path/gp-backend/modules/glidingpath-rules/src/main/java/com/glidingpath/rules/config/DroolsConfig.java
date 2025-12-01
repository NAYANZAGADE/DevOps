package com.glidingpath.rules.config;

import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.glidingpath.rules.entity.RuleConfig;
import com.glidingpath.rules.repository.RuleConfigRepository;

@Configuration
@ConditionalOnClass(KieServices.class)
public class DroolsConfig {
    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);

    @Autowired
    private RuleConfigRepository ruleConfigRepository;

    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.get();
        try {
            KieFileSystem kfs = kieServices.newKieFileSystem();
            
            // Load all rules from database
            List<RuleConfig> allRules = ruleConfigRepository.findAll();
            if (allRules.isEmpty()) {
                log.error("No rules found in database - Drools will not be available");
                throw new IllegalStateException("No rules found in database for Drools");
            }
            
            log.info("Loading {} rules from database", allRules.size());
            
            // Write each rule to the KieFileSystem
            for (RuleConfig ruleConfig : allRules) {
                String ruleFileName = "src/main/resources/rules/" + ruleConfig.getName() + ".drl";
                kfs.write(ruleFileName, ruleConfig.getRuleContent());
                log.info("Loaded rule: {} with content length: {}", ruleConfig.getName(), ruleConfig.getRuleContent().length());
            }
            
            // Generate kmodule.xml with multiple kbase configurations
            StringBuilder kmoduleContent = new StringBuilder();
            kmoduleContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            kmoduleContent.append("<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n");
            
            // Create a kbase for each rule set
            for (RuleConfig ruleConfig : allRules) {
                kmoduleContent.append("  <kbase name=\"").append(ruleConfig.getName()).append("-rules\" packages=\"rules\">\n");
                kmoduleContent.append("    <ksession name=\"ksession-").append(ruleConfig.getName()).append("\" type=\"stateless\"/>\n");
                kmoduleContent.append("  </kbase>\n");
            }
            
            kmoduleContent.append("</kmodule>");
            
            kfs.write("src/main/resources/META-INF/kmodule.xml", kmoduleContent.toString());
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
            kieBuilder.buildAll();
            
            if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
                log.error("Drools build errors: {}", kieBuilder.getResults().getMessages());
                throw new IllegalStateException("Drools build errors: " + kieBuilder.getResults().getMessages());
            }
            
            KieContainer container = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
            log.info("Successfully created KieContainer with bases: {}", container.getKieBaseNames());
            return container;
            
        } catch (Exception e) {
            log.error("Failed to create KieContainer: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to create KieContainer", e);
        }
    }
} 