package com.sporty.bet_jackpot.dto;

import com.sporty.bet_jackpot.validation.ValidBetRequest;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidBetRequest
public class BetRequest {
    @NotBlank(message = "Bet ID cannot be blank")
    @Size(min = 1, max = 50, message = "Bet ID must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Bet ID can only contain alphanumeric characters, underscores, and hyphens")
    private String betId; // This will be an FK/UUID in real-word production apps
    
    @NotBlank(message = "User ID cannot be blank")
    @Size(min = 1, max = 50, message = "User ID must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "User ID can only contain alphanumeric characters, underscores, and hyphens")
    private String userId;
    
    @NotNull(message = "Jackpot ID cannot be null")
    @Positive(message = "Jackpot ID must be positive")
    private Long jackpotId;
    
    @NotNull(message = "Bet amount cannot be null")
    @Positive(message = "Bet amount must be positive")
    @DecimalMin(value = "0.01", message = "Bet amount must be at least 0.01")
    @DecimalMax(value = "1000000.00", message = "Bet amount cannot exceed 1,000,000.00")
    @Digits(integer = 7, fraction = 2, message = "Bet amount must have at most 7 integer digits and 2 decimal places")
    private BigDecimal betAmount;
}