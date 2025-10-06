package com.sporty.bet_jackpot.strategy.contribution;

import com.sporty.bet_jackpot.model.Jackpot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariableContributionStrategy implements ContributionStrategy {

    @Value("${jackpot.config.variable-contribution-initial-percentage}")
    private Double initialPercentage;

    @Value("${jackpot.config.variable-contribution-min-percentage}")
    private Double minPercentage; // 2

    @Value("${jackpot.config.variable-reward-pool-limit}")
    private Double poolLimit;

    @Override
    public BigDecimal calculateContribution(BigDecimal betAmount, Jackpot jackpot) {
        // Formula: starts high, decreases as pool grows
        // Example: percentage = initial - (current_pool / initial_pool) * (initial - min)

        BigDecimal poolRatio = jackpot.getCurrentPoolValue()
                // .divide(jackpot.getInitialPoolValue(), 4, RoundingMode.HALF_UP);
                .divide(BigDecimal.valueOf(poolLimit), 4, RoundingMode.HALF_UP);

        double currentPercentage = initialPercentage -
                (poolRatio.doubleValue() * (initialPercentage - minPercentage));

        currentPercentage = Math.max(currentPercentage, minPercentage);

        return betAmount.multiply(BigDecimal.valueOf(currentPercentage / 100));
    }
}