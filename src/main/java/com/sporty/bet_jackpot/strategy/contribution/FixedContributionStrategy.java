package com.sporty.bet_jackpot.strategy.contribution;

import com.sporty.bet_jackpot.model.Jackpot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedContributionStrategy implements ContributionStrategy {
    
    @Value("${jackpot.config.fixed-contribution-percentage}")
    private Double fixedPercentage;
    
    @Override
    public BigDecimal calculateContribution(BigDecimal betAmount, Jackpot jackpot) {
        // Use jackpot's config or default
        Double percentage = jackpot.getContributionPercentage() != null 
            ? jackpot.getContributionPercentage() 
            : fixedPercentage;
        
        return betAmount.multiply(BigDecimal.valueOf(percentage / 100));
    }
}