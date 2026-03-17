package com.aistock.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@EnableScheduling
public class PriceUpdateService {
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();
    private Map<String, Double> prices = new HashMap<>();

    public PriceUpdateService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        prices.put("AAPL", 150.0);
        prices.put("GOOGL", 2800.0);
        prices.put("TSLA", 700.0);
    }

    @Scheduled(fixedDelay = 1000)
    public void sendPriceUpdates() {
        // Update prices with random walk
        for (String symbol : prices.keySet()) {
            double change = (random.nextDouble() - 0.5) * 2.0;
            double newPrice = prices.get(symbol) + change;
            if (newPrice < 1) newPrice = 1;
            prices.put(symbol, newPrice);
        }
        // Broadcast to all clients
        messagingTemplate.convertAndSend("/topic/prices", prices);
    }
}
