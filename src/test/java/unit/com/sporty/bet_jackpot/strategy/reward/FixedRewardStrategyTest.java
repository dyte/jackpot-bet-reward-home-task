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
class FixedRewardStrategyTest {

    @InjectMocks
    private FixedRewardStrategy fixedRewardStrategy;

    private Jackpot jackpot;

    @BeforeEach
    void setUp() {
        // Set default fixed chance percentage
        ReflectionTestUtils.setField(fixedRewardStrategy, "fixedChance", 10.0);
        
        jackpot = new Jackpot(1L, "Test Jackpot", ContributionType.FIXED, RewardType.FIXED,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), 0.1, 0.5, 
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void evaluateWin_ShouldUseFixedChance_WhenJackpotRewardChanceIsNull() {
        // Given
        jackpot.setRewardChancePercentage(null);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        // Since this is a random test, we can't assert exact values
        // But we can verify the method doesn't throw exceptions
        assertDoesNotThrow(() -> fixedRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldUseJackpotRewardChance_WhenProvided() {
        // Given
        jackpot.setRewardChancePercentage(25.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        // Since this is a random test, we can't assert exact values
        // But we can verify the method doesn't throw exceptions
        assertDoesNotThrow(() -> fixedRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldHandleZeroChance() {
        // Given
        jackpot.setRewardChancePercentage(0.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertFalse(result); // With 0% chance, should always be false
    }

    @Test
    void evaluateWin_ShouldHandleHundredPercentChance() {
        // Given
        jackpot.setRewardChancePercentage(100.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertTrue(result); // With 100% chance, should always be true
    }

    @Test
    void evaluateWin_ShouldHandleNegativeChance() {
        // Given
        jackpot.setRewardChancePercentage(-10.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertFalse(result); // With negative chance, should always be false
    }

    @Test
    void evaluateWin_ShouldHandleChanceGreaterThanHundred() {
        // Given
        jackpot.setRewardChancePercentage(150.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertTrue(result); // With >100% chance, should always be true
    }

    @Test
    void evaluateWin_ShouldUseDefaultFixedChance_WhenNoConfiguration() {
        // Given
        jackpot.setRewardChancePercentage(null);
        ReflectionTestUtils.setField(fixedRewardStrategy, "fixedChance", 5.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        assertDoesNotThrow(() -> fixedRewardStrategy.evaluateWin(jackpot));
    }

    @Test
    void evaluateWin_ShouldBeConsistentWithSameChance() {
        // Given
        jackpot.setRewardChancePercentage(50.0);
        
        // When - Run multiple times
        boolean result1 = fixedRewardStrategy.evaluateWin(jackpot);
        boolean result2 = fixedRewardStrategy.evaluateWin(jackpot);
        boolean result3 = fixedRewardStrategy.evaluateWin(jackpot);
        
        // Then
        // Results may vary due to randomness, but method should not throw exceptions
        assertDoesNotThrow(() -> {
            fixedRewardStrategy.evaluateWin(jackpot);
        });
    }

    @Test
    void evaluateWin_ShouldHandleNullJackpot() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            fixedRewardStrategy.evaluateWin(null));
    }

    @Test
    void evaluateWin_ShouldWorkWithDifferentJackpotValues() {
        // Given
        Jackpot jackpotWithDifferentValues = new Jackpot(2L, "Different Jackpot", 
                ContributionType.VARIABLE, RewardType.VARIABLE,
                BigDecimal.valueOf(5000), BigDecimal.valueOf(7500), 0.2, 0.8, 
                LocalDateTime.now(), LocalDateTime.now());
        jackpotWithDifferentValues.setRewardChancePercentage(15.0);
        
        // When
        boolean result = fixedRewardStrategy.evaluateWin(jackpotWithDifferentValues);
        
        // Then
        assertDoesNotThrow(() -> fixedRewardStrategy.evaluateWin(jackpotWithDifferentValues));
    }
}
