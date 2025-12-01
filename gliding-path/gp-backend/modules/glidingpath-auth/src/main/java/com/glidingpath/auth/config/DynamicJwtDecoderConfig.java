package com.glidingpath.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@Profile("!local")
public class DynamicJwtDecoderConfig {

	@Bean(name = "staticIssuerJwtDecoder")
	@ConditionalOnMissingBean(JwtDecoder.class)
	@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.issuer-uri")
	public JwtDecoder issuerJwtDecoder(Environment environment) {
		String issuerUri = environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
		return JwtDecoders.fromIssuerLocation(issuerUri);
	}

	@Bean(name = "staticJwkJwtDecoder")
	@ConditionalOnMissingBean(JwtDecoder.class)
	@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri")
	public JwtDecoder jwkSetJwtDecoder(Environment environment) {
		String jwkSetUri = environment.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}
}