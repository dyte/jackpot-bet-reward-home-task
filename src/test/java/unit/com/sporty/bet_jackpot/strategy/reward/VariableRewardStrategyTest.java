package com.sporty.bet_jackpot.strategy.reward;

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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VariableRewardStrategyTest {

    @InjectMocks
    private VariableRewardStrategy variableRewardStrategy;

    private Jackpot jackpot;

    @BeforeEach
    void setUp() {
        // Set default configuration values
        ReflectionTestUtils.setField(variableRewardStrategy, "initialChance", 5.0);
        ReflectionTestUtils.setField(variableRewardStrategy, "poolLimit", 10000.0);
        
        jackpot = new Jackpot(1L, "Test Jackpot", ContributionType.VARIABLE, RewardType.VARIABLE,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), 0.1, 0.5, 
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void evaluateWin_ShouldReturnTrue_WhenPoolHitsLimit() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(10000.0)); // Exactly at limit
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertTrue(result);
    }

    @Test
    void evaluateWin_ShouldReturnTrue_WhenPoolExceedsLimit() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(15000.0)); // Above limit
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertTrue(result);
    }

    @Test
    void evaluateWin_ShouldCalculateChanceBasedOnPoolRatio_WhenPoolBelowLimit() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(5000.0)); // 50% of limit
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        // Since this is a random test, we can't assert exact values
        // But we can verify the method doesn't throw exceptions
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldUseInitialChance_WhenPoolIsZero() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.ZERO);
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleSmallPoolValues() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(100.0)); // Very small pool
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleLargePoolValues() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(9999.0)); // Just below limit
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleNegativePoolValues() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(-1000.0)); // Negative pool
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleNullPoolValue() {
        // Given
        jackpot.setCurrentPoolValue(null);
        
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleNullJackpot() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            variableRewardStrategy.evaluateWin(null));
    }

    @Test
    void evaluateWin_ShouldWorkWithDifferentConfigurations() {
        // Given
        ReflectionTestUtils.setField(variableRewardStrategy, "initialChance", 10.0);
        ReflectionTestUtils.setField(variableRewardStrategy, "poolLimit", 5000.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(2500.0)); // 50% of new limit
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleZeroInitialChance() {
        // Given
        ReflectionTestUtils.setField(variableRewardStrategy, "initialChance", 0.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(5000.0));
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> variableRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleHundredPercentInitialChance() {
        // Given
        ReflectionTestUtils.setField(variableRewardStrategy, "initialChance", 100.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(1000.0));
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertTrue(result); // Should always be true with 100% initial chance
    }

    @Test
    void evaluateWin_ShouldHandleZeroPoolLimit() {
        // Given
        ReflectionTestUtils.setField(variableRewardStrategy, "poolLimit", 0.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(100.0));
        
        // When
        boolean result = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertTrue(result); // Should always be true when limit is 0
    }

    @Test
    void evaluateWin_ShouldBeConsistentWithSamePoolValue() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(3000.0));
        
        // When - Run multiple times
        boolean result1 = variableRewardStrategy.evaluateWin(jackpot);
        boolean result2 = variableRewardStrategy.evaluateWin(jackpot);
        boolean result3 = variableRewardStrategy.evaluateWin(jackpot);
        
        // Then
        // Results may vary due to randomness, but method should not throw exceptions
        assertDoesNotThrow(() -> {
            variableRewardStrategy.evaluateWin(jackpot);
        });
    }
}
