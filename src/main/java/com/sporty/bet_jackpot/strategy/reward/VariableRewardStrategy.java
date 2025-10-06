package com.sporty.bet_jackpot.strategy.reward;

import com.sporty.bet_jackpot.model.Jackpot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariableRewardStrategy implements RewardStrategy {
    
    @Value("${jackpot.config.variable-reward-initial-chance-percentage}")
    private Double initialChance;
    
    @Value("${jackpot.config.variable-reward-pool-limit}")
    private Double poolLimit;
    
    @Override
    public boolean evaluateWin(Jackpot jackpot) {
        // If pool hits limit, 100% chance
        if (jackpot.getCurrentPoolValue().compareTo(BigDecimal.valueOf(poolLimit)) >= 0) {
            return true;
        }
        
        // Otherwise, chance increases with pool size
        BigDecimal poolRatio = jackpot.getCurrentPoolValue()
            .divide(BigDecimal.valueOf(poolLimit), 4, RoundingMode.HALF_UP);
        
        double currentChance = initialChance + (poolRatio.doubleValue() * (100 - initialChance));
        
        double random = Math.random() * 100;
        return random < currentChance;
    }
}