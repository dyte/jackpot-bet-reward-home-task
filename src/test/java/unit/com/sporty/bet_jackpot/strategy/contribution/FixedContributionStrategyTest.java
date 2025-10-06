package com.sporty.bet_jackpot.strategy.contribution;

import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.model.Jackpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FixedContributionStrategyTest {

    @InjectMocks
    private FixedContributionStrategy fixedContributionStrategy;

    private Jackpot jackpot;
    private BigDecimal betAmount;

    @BeforeEach
    void setUp() {
        // Set default fixed percentage
        ReflectionTestUtils.setField(fixedContributionStrategy, "fixedPercentage", 10.0);
        
        jackpot = new Jackpot(1L, "Test Jackpot", ContributionType.FIXED, RewardType.FIXED,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), 0.1, 0.5, 
                LocalDateTime.now(), LocalDateTime.now());
        
        betAmount = BigDecimal.valueOf(100.0);
    }

    @Test
    void calculateContribution_ShouldUseFixedPercentage_WhenJackpotContributionPercentageIsNull() {
        // Given
        jackpot.setContributionPercentage(null);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(10.0).setScale(2), result.setScale(2)); // 10% of 100
    }

    @Test
    void calculateContribution_ShouldUseJackpotContributionPercentage_WhenProvided() {
        // Given
        jackpot.setContributionPercentage(15.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(15.0).setScale(2), result.setScale(2)); // 15% of 100
    }

    @Test
    void calculateContribution_ShouldHandleZeroPercentage() {
        // Given
        jackpot.setContributionPercentage(0.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.ZERO.setScale(2), result.setScale(2));
    }

    @Test
    void calculateContribution_ShouldHandleHundredPercentPercentage() {
        // Given
        jackpot.setContributionPercentage(100.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(100.0).setScale(2), result.setScale(2)); // 100% of 100
    }

    @Test
    void calculateContribution_ShouldHandleDecimalPercentages() {
        // Given
        jackpot.setContributionPercentage(12.5);
        BigDecimal betAmount = BigDecimal.valueOf(200.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(25.0).setScale(2), result.setScale(2)); // 12.5% of 200
    }

    @Test
    void calculateContribution_ShouldHandleZeroBetAmount() {
        // Given
        BigDecimal zeroBetAmount = BigDecimal.ZERO;
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(zeroBetAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.ZERO.setScale(2), result.setScale(2));
    }

    @Test
    void calculateContribution_ShouldHandleNegativeBetAmount() {
        // Given
        BigDecimal negativeBetAmount = BigDecimal.valueOf(-100.0);
        jackpot.setContributionPercentage(null); // Use fixed percentage
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(negativeBetAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(-10.0).setScale(2), result.setScale(2)); // 10% of -100
    }

    @Test
    void calculateContribution_ShouldHandleLargeBetAmounts() {
        // Given
        BigDecimal largeBetAmount = BigDecimal.valueOf(1000000.0);
        jackpot.setContributionPercentage(null); // Use fixed percentage
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(largeBetAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(100000.0).setScale(2), result.setScale(2)); // 10% of 1,000,000
    }

    @Test
    void calculateContribution_ShouldHandleSmallBetAmounts() {
        // Given
        BigDecimal smallBetAmount = BigDecimal.valueOf(0.01);
        jackpot.setContributionPercentage(null); // Use fixed percentage
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(smallBetAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(0.001).setScale(3), result.setScale(3)); // 10% of 0.01
    }

    @Test
    void calculateContribution_ShouldHandleNullBetAmount() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            fixedContributionStrategy.calculateContribution(null, jackpot));
    }

    @Test
    void calculateContribution_ShouldHandleNullJackpot() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            fixedContributionStrategy.calculateContribution(betAmount, null));
    }

    @Test
    void calculateContribution_ShouldUseDefaultFixedPercentage_WhenNoConfiguration() {
        // Given
        jackpot.setContributionPercentage(null);
        ReflectionTestUtils.setField(fixedContributionStrategy, "fixedPercentage", 5.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(5.0).setScale(2), result.setScale(2)); // 5% of 100
    }

    @Test
    void calculateContribution_ShouldHandleDifferentJackpotTypes() {
        // Given
        Jackpot variableJackpot = new Jackpot(2L, "Variable Jackpot", 
                ContributionType.VARIABLE, RewardType.VARIABLE,
                BigDecimal.valueOf(2000), BigDecimal.valueOf(3000), 0.2, 0.6, 
                LocalDateTime.now(), LocalDateTime.now());
        variableJackpot.setContributionPercentage(20.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, variableJackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(20.0).setScale(2), result.setScale(2)); // 20% of 100
    }

    @Test
    void calculateContribution_ShouldHandlePrecisionCorrectly() {
        // Given
        jackpot.setContributionPercentage(33.333);
        BigDecimal betAmount = BigDecimal.valueOf(300.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(99.999).setScale(3, RoundingMode.HALF_UP), result.setScale(3, RoundingMode.HALF_UP)); // 33.333% of 300
    }

    @Test
    void calculateContribution_ShouldHandleVerySmallPercentages() {
        // Given
        jackpot.setContributionPercentage(0.01);
        BigDecimal betAmount = BigDecimal.valueOf(1000.0);
        
        // When
        BigDecimal result = fixedContributionStrategy.calculateContribution(betAmount, jackpot);
        
        // Then
        assertEquals(BigDecimal.valueOf(0.1).setScale(1), result.setScale(1)); // 0.01% of 1000
    }
}
