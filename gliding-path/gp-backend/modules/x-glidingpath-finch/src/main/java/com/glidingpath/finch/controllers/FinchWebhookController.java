package com.glidingpath.finch.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.glidingpath.finch.service.FinchWebhookService;
import java.util.Map;

@RestController
@RequestMapping("/webhook/finch")
public class FinchWebhookController {

    @Autowired
    private FinchWebhookService finchWebhookService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
        @RequestBody String body,
        @RequestHeader Map<String, String> headers
    ) {
        finchWebhookService.handleWebhook(body, headers);
        return ResponseEntity.ok("Webhook received");
    }
} 