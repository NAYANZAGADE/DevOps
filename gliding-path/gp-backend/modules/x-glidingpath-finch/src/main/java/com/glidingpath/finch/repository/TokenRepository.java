package com.glidingpath.finch.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glidingpath.finch.entity.TokenEntity;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {
	Optional<TokenEntity> findByTenantId(String tenantId);
}
