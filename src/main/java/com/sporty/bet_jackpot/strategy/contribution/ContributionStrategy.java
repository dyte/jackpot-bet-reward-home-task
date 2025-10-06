package com.sporty.bet_jackpot.strategy.contribution;

import com.sporty.bet_jackpot.model.Jackpot;

import java.math.BigDecimal;

public interface ContributionStrategy {
    BigDecimal calculateContribution(BigDecimal betAmount, Jackpot jackpot);
}