package com.sporty.bet_jackpot.builder;

import com.sporty.bet_jackpot.dto.BetRequest;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Builder pattern implementation for creating BetRequest DTOs.
 * Provides a fluent interface for constructing BetRequest objects with validation.
 */
public class BetRequestBuilder {
    
    private String betId;
    private String userId;
    private Long jackpotId;
    private BigDecimal betAmount;
    
    private BetRequestBuilder() {
        // Private constructor to force use of static factory method
    }
    
    /**
     * Creates a new BetRequestBuilder instance.
     * @return new BetRequestBuilder instance
     */
    public static BetRequestBuilder newBetRequest() {
        return new BetRequestBuilder();
    }
    
    /**
     * Creates a new BetRequestBuilder instance with a generated bet ID.
     * @return new BetRequestBuilder instance with auto-generated bet ID
     */
    public static BetRequestBuilder newBetRequestWithGeneratedId() {
        return new BetRequestBuilder().withBetId(UUID.randomUUID().toString());
    }
    
    /**
     * Sets the bet ID.
     * @param betId the bet ID
     * @return this builder instance
     */
    public BetRequestBuilder withBetId(String betId) {
        this.betId = betId;
        return this;
    }
    
    /**
     * Generates a new bet ID using UUID.
     * @return this builder instance
     */
    public BetRequestBuilder withGeneratedBetId() {
        this.betId = UUID.randomUUID().toString();
        return this;
    }
    
    /**
     * Sets the user ID.
     * @param userId the user ID
     * @return this builder instance
     */
    public BetRequestBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }
    
    /**
     * Sets the jackpot ID.
     * @param jackpotId the jackpot ID
     * @return this builder instance
     */
    public BetRequestBuilder withJackpotId(Long jackpotId) {
        this.jackpotId = jackpotId;
        return this;
    }
    
    /**
     * Sets the bet amount.
     * @param betAmount the bet amount
     * @return this builder instance
     */
    public BetRequestBuilder withBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
        return this;
    }
    
    /**
     * Sets the bet amount from a double.
     * @param betAmount the bet amount
     * @return this builder instance
     */
    public BetRequestBuilder withBetAmount(double betAmount) {
        return withBetAmount(BigDecimal.valueOf(betAmount));
    }
    
    /**
     * Sets the bet amount from a string.
     * @param betAmount the bet amount as string
     * @return this builder instance
     */
    public BetRequestBuilder withBetAmount(String betAmount) {
        return withBetAmount(new BigDecimal(betAmount));
    }
    
    /**
     * Builds the BetRequest DTO with validation.
     * @return the constructed BetRequest DTO
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public BetRequest build() {
        validate();
        
        return new BetRequest(betId, userId, jackpotId, betAmount);
    }
    
    /**
     * Validates the builder state before constructing the BetRequest.
     * @throws IllegalArgumentException if validation fails
     */
    private void validate() {
        if (betId == null || betId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bet ID is required");
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        if (jackpotId == null || jackpotId <= 0) {
            throw new IllegalArgumentException("Jackpot ID must be positive");
        }
        
        if (betAmount == null || betAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive");
        }
        
        if (betAmount.compareTo(new BigDecimal("1000000.00")) > 0) {
            throw new IllegalArgumentException("Bet amount cannot exceed 1,000,000.00");
        }
    }
}
