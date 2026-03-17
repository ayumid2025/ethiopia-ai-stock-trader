package com.aistock.backend.controller;

import com.aistock.backend.dto.SignalRequest;
import com.aistock.backend.service.TradingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Value("${ai.api.key}")
    private String expectedApiKey;

    private final TradingService tradingService;

    public AIController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping("/signal")
    public void receiveSignal(@RequestHeader("X-API-Key") String apiKey, @RequestBody SignalRequest signal) {
        if (!expectedApiKey.equals(apiKey)) {
            throw new RuntimeException("Invalid API key");
        }
        // For demo, assume user ID 1 (you'd map API key to user)
        tradingService.processSignal(1L, signal);
    }
}
