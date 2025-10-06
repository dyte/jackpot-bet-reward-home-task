package com.sporty.bet_jackpot.builder;

import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.model.Jackpot;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Builder pattern implementation for creating Jackpot entities.
 * Provides a fluent interface for constructing Jackpot objects with validation.
 */
public class JackpotBuilder {
    
    private String name;
    private ContributionType contributionType;
    private RewardType rewardType;
    private BigDecimal initialPoolValue;
    private BigDecimal currentPoolValue;
    private Double contributionPercentage;
    private Double rewardChancePercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private JackpotBuilder() {
        // Private constructor to force use of static factory method
    }
    
    /**
     * Creates a new JackpotBuilder instance.
     * @return new JackpotBuilder instance
     */
    public static JackpotBuilder newJackpot() {
        return new JackpotBuilder();
    }
    
    /**
     * Creates a new JackpotBuilder instance with a name.
     * @param name the jackpot name
     * @return new JackpotBuilder instance
     */
    public static JackpotBuilder newJackpot(String name) {
        return new JackpotBuilder().withName(name);
    }
    
    /**
     * Sets the jackpot name.
     * @param name the jackpot name
     * @return this builder instance
     */
    public JackpotBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Sets the contribution type to FIXED.
     * @return this builder instance
     */
    public JackpotBuilder withFixedContribution() {
        this.contributionType = ContributionType.FIXED;
        return this;
    }
    
    /**
     * Sets the contribution type to VARIABLE.
     * @return this builder instance
     */
    public JackpotBuilder withVariableContribution() {
        this.contributionType = ContributionType.VARIABLE;
        return this;
    }
    
    /**
     * Sets the contribution type.
     * @param contributionType the contribution type
     * @return this builder instance
     */
    public JackpotBuilder withContributionType(ContributionType contributionType) {
        this.contributionType = contributionType;
        return this;
    }
    
    /**
     * Sets the reward type to FIXED.
     * @return this builder instance
     */
    public JackpotBuilder withFixedReward() {
        this.rewardType = RewardType.FIXED;
        return this;
    }
    
    /**
     * Sets the reward type to VARIABLE.
     * @return this builder instance
     */
    public JackpotBuilder withVariableReward() {
        this.rewardType = RewardType.VARIABLE;
        return this;
    }
    
    /**
     * Sets the reward type.
     * @param rewardType the reward type
     * @return this builder instance
     */
    public JackpotBuilder withRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
        return this;
    }
    
    /**
     * Sets the initial pool value.
     * @param initialPoolValue the initial pool value
     * @return this builder instance
     */
    public JackpotBuilder withInitialPoolValue(BigDecimal initialPoolValue) {
        this.initialPoolValue = initialPoolValue;
        return this;
    }
    
    /**
     * Sets the initial pool value from a double.
     * @param initialPoolValue the initial pool value
     * @return this builder instance
     */
    public JackpotBuilder withInitialPoolValue(double initialPoolValue) {
        return withInitialPoolValue(BigDecimal.valueOf(initialPoolValue));
    }
    
    /**
     * Sets the current pool value.
     * @param currentPoolValue the current pool value
     * @return this builder instance
     */
    public JackpotBuilder withCurrentPoolValue(BigDecimal currentPoolValue) {
        this.currentPoolValue = currentPoolValue;
        return this;
    }
    
    /**
     * Sets the current pool value from a double.
     * @param currentPoolValue the current pool value
     * @return this builder instance
     */
    public JackpotBuilder withCurrentPoolValue(double currentPoolValue) {
        return withCurrentPoolValue(BigDecimal.valueOf(currentPoolValue));
    }
    
    /**
     * Sets both initial and current pool values to the same value.
     * @param poolValue the pool value
     * @return this builder instance
     */
    public JackpotBuilder withPoolValue(BigDecimal poolValue) {
        this.initialPoolValue = poolValue;
        this.currentPoolValue = poolValue;
        return this;
    }
    
    /**
     * Sets both initial and current pool values to the same value from a double.
     * @param poolValue the pool value
     * @return this builder instance
     */
    public JackpotBuilder withPoolValue(double poolValue) {
        return withPoolValue(BigDecimal.valueOf(poolValue));
    }
    
    /**
     * Sets the contribution percentage.
     * @param contributionPercentage the contribution percentage
     * @return this builder instance
     */
    public JackpotBuilder withContributionPercentage(Double contributionPercentage) {
        this.contributionPercentage = contributionPercentage;
        return this;
    }
    
    /**
     * Sets the reward chance percentage.
     * @param rewardChancePercentage the reward chance percentage
     * @return this builder instance
     */
    public JackpotBuilder withRewardChancePercentage(Double rewardChancePercentage) {
        this.rewardChancePercentage = rewardChancePercentage;
        return this;
    }
    
    /**
     * Sets the creation timestamp.
     * @param createdAt the creation timestamp
     * @return this builder instance
     */
    public JackpotBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    /**
     * Sets the update timestamp.
     * @param updatedAt the update timestamp
     * @return this builder instance
     */
    public JackpotBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    /**
     * Sets the current timestamp as both creation and update time.
     * @return this builder instance
     */
    public JackpotBuilder withCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        return this;
    }
    
    /**
     * Builds the Jackpot entity with validation.
     * @return the constructed Jackpot entity
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public Jackpot build() {
        validate();
        
        Jackpot jackpot = new Jackpot();
        jackpot.setName(name);
        jackpot.setContributionType(contributionType);
        jackpot.setRewardType(rewardType);
        jackpot.setInitialPoolValue(initialPoolValue);
        jackpot.setCurrentPoolValue(currentPoolValue);
        jackpot.setContributionPercentage(contributionPercentage);
        jackpot.setRewardChancePercentage(rewardChancePercentage);
        jackpot.setCreatedAt(createdAt);
        jackpot.setUpdatedAt(updatedAt);
        
        return jackpot;
    }
    
    /**
     * Validates the builder state before constructing the Jackpot.
     * @throws IllegalArgumentException if validation fails
     */
    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Jackpot name is required");
        }
        
        if (contributionType == null) {
            throw new IllegalArgumentException("Contribution type is required");
        }
        
        if (rewardType == null) {
            throw new IllegalArgumentException("Reward type is required");
        }
        
        if (initialPoolValue == null || initialPoolValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Initial pool value must be positive");
        }
        
        if (currentPoolValue == null || currentPoolValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Current pool value must be positive");
        }
        
        if (contributionPercentage != null && (contributionPercentage < 0 || contributionPercentage > 100)) {
            throw new IllegalArgumentException("Contribution percentage must be between 0 and 100");
        }
        
        if (rewardChancePercentage != null && (rewardChancePercentage < 0 || rewardChancePercentage > 100)) {
            throw new IllegalArgumentException("Reward chance percentage must be between 0 and 100");
        }
    }
}
