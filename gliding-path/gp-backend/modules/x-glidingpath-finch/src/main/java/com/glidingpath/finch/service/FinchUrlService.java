package com.glidingpath.finch.service;
import com.glidingpath.finch.dto.FinchUrlDTO;

public interface FinchUrlService {
    FinchUrlDTO generateFinchConnectUrl(String tenantId);
}