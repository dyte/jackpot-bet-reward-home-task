package com.sporty.bet_jackpot.strategy.reward;

import com.sporty.bet_jackpot.model.Jackpot;

public interface RewardStrategy {
    boolean evaluateWin(Jackpot jackpot);
}