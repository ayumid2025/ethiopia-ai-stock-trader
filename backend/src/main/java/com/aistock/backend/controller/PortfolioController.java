package com.aistock.backend.controller;

import com.aistock.backend.model.Portfolio;
import com.aistock.backend.model.User;
import com.aistock.backend.repository.PortfolioRepository;
import com.aistock.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioController(PortfolioRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<Portfolio> getPortfolio() {
        User user = getCurrentUser();
        return portfolioRepository.findByUser(user);
    }
}
