package com.glidingpath.platform.sponsor.service;

import com.glidingpath.common.dto.CommonEmailRequest;

public interface NotificationService {
	boolean sendEmail(CommonEmailRequest request);
} 