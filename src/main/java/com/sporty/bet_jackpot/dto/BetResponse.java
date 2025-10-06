package com.sporty.bet_jackpot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BetResponse {
    private String betId;
    private String message;
    private String status;
}