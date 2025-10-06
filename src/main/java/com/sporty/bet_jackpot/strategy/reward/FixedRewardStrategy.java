package com.sporty.bet_jackpot.strategy.reward;

import com.sporty.bet_jackpot.model.Jackpot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FixedRewardStrategy implements RewardStrategy {
    
    @Value("${jackpot.config.fixed-reward-chance-percentage}")
    private Double fixedChance;
    
    @Override
    public boolean evaluateWin(Jackpot jackpot) {
        Double chance = jackpot.getRewardChancePercentage() != null 
            ? jackpot.getRewardChancePercentage() 
            : fixedChance;
        
        double random = Math.random() * 100;
        return random < chance;
    }
}