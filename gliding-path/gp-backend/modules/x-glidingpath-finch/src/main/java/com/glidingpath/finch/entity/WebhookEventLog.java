package com.glidingpath.finch.entity;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(name = "webhook_event_log")
public class WebhookEventLog {
    @Id
    private String eventId;
    private Instant processedAt;
    private String requestBody;
    private String responseBody;
} 