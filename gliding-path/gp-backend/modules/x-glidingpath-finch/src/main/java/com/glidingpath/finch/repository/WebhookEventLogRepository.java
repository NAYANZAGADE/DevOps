package com.glidingpath.finch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glidingpath.finch.entity.WebhookEventLog;

@Repository
public interface WebhookEventLogRepository extends JpaRepository<WebhookEventLog, String> {
    boolean existsByEventId(String eventId);
} 