package com.sporty.bet_jackpot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RewardResponse {
    private String betId;
    private boolean isWinner;
    private BigDecimal rewardAmount;
    private String message;
}