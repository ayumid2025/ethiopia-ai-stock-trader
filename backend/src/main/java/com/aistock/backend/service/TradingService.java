package com.aistock.backend.service;

import com.aistock.backend.model.*;
import com.aistock.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

public void processSignal(Long userId, SignalRequest signal) {
    if ("BUY".equalsIgnoreCase(signal.getAction())) {
        buyOrder(userId, signal.getSymbol(), signal.getPrice(), signal.getQuantity());
    } else if ("SELL".equalsIgnoreCase(signal.getAction())) {
        sellOrder(userId, signal.getSymbol(), signal.getPrice(), signal.getQuantity());
    }
}
@Service
public class TradingService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final OrderRepository orderRepository;

    public TradingService(UserRepository userRepository, PortfolioRepository portfolioRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void buyOrder(Long userId, String symbol, BigDecimal price, BigDecimal quantity) {
        User user = userRepository.findById(userId).orElseThrow();
        BigDecimal cost = price.multiply(quantity);
        if (user.getCashBalance().compareTo(cost) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Deduct cash
        user.setCashBalance(user.getCashBalance().subtract(cost));
        userRepository.save(user);

        // Update portfolio
        Portfolio portfolio = portfolioRepository.findByUserAndSymbol(user, symbol)
                .orElse(new Portfolio());
        if (portfolio.getId() == null) {
            portfolio.setUser(user);
            portfolio.setSymbol(symbol);
            portfolio.setQuantity(quantity);
            portfolio.setAverageBuyPrice(price);
        } else {
            // Weighted average for multiple buys
            BigDecimal totalValue = portfolio.getAverageBuyPrice().multiply(portfolio.getQuantity())
                                    .add(price.multiply(quantity));
            BigDecimal totalQty = portfolio.getQuantity().add(quantity);
            portfolio.setAverageBuyPrice(totalValue.divide(totalQty, BigDecimal.ROUND_HALF_UP));
            portfolio.setQuantity(totalQty);
        }
        portfolioRepository.save(portfolio);

        // Record order
        Order order = new Order();
        order.setUser(user);
        order.setSymbol(symbol);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setType("BUY");
        orderRepository.save(order);
    }

    @Transactional
    public void sellOrder(Long userId, String symbol, BigDecimal price, BigDecimal quantity) {
        User user = userRepository.findById(userId).orElseThrow();
        Portfolio portfolio = portfolioRepository.findByUserAndSymbol(user, symbol)
                .orElseThrow(() -> new RuntimeException("No shares to sell"));
        if (portfolio.getQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient shares");
        }

        // Add cash
        BigDecimal proceeds = price.multiply(quantity);
        user.setCashBalance(user.getCashBalance().add(proceeds));
        userRepository.save(user);

        // Update portfolio
        portfolio.setQuantity(portfolio.getQuantity().subtract(quantity));
        if (portfolio.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolioRepository.save(portfolio);
        }

        // Record order
        Order order = new Order();
        order.setUser(user);
        order.setSymbol(symbol);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setType("SELL");
        orderRepository.save(order);
    }
}
