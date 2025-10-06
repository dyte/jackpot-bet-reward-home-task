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
class VariableContributionStrategyTest {

    @InjectMocks
    private VariableContributionStrategy variableContributionStrategy;

    private Jackpot jackpot;
    private BigDecimal betAmount;

    @BeforeEach
    void setUp() {
        // Initialize the strategy manually since @Value fields won't be injected in unit tests
        variableContributionStrategy = new VariableContributionStrategy();

        // Set default configuration values
        ReflectionTestUtils.setField(variableContributionStrategy, "initialPercentage", 20.0);
        ReflectionTestUtils.setField(variableContributionStrategy, "minPercentage", 5.0);
        ReflectionTestUtils.setField(variableContributionStrategy, "poolLimit", 10000.0);

        jackpot = new Jackpot(1L, "Test Jackpot", ContributionType.VARIABLE, RewardType.VARIABLE,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), 0.1, 0.5,
                LocalDateTime.now(), LocalDateTime.now());

        betAmount = BigDecimal.valueOf(100.0);
    }

    @Test
    void calculateContribution_ShouldUseInitialPercentage_WhenPoolIsAtInitialValue() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.ZERO); // Zero pool should use initial percentage

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(20.0).setScale(2), result.setScale(2)); // 20% of 100
    }

    @Test
    void calculateContribution_ShouldUseMinPercentage_WhenPoolEqualsInitial() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(10000.0)); // Same as pool limit

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(5.0).setScale(2), result.setScale(2)); // Min percentage (5%) of 100
    }

    @Test
    void calculateContribution_ShouldUseMinPercentage_WhenPoolIsDoubleInitial() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(20000.0)); // Double pool limit

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(5.0).setScale(2), result.setScale(2)); // Min percentage (5%) of 100
    }

    @Test
    void calculateContribution_ShouldCalculateCorrectly_WhenPoolIsHalfInitial() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(5000.0)); // Half pool limit

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        // Should be between initial and min: 20 - (0.5 * (20 - 5)) = 20 - 7.5 = 12.5%
        assertEquals(BigDecimal.valueOf(12.5).setScale(2), result.setScale(2));
    }

    @Test
    void calculateContribution_ShouldNotGoBelowMinPercentage_WhenPoolExceedsDoubleInitial() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(30000.0)); // Triple pool limit

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(5.0).setScale(2), result.setScale(2)); // Should cap at min percentage
    }

    @Test
    void calculateContribution_ShouldHandleZeroCurrentPool() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.ZERO);

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(20.0).setScale(2), result.setScale(2)); // Should use initial percentage
    }

    @Test
    void calculateContribution_ShouldHandleZeroPoolLimit() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(100.0));
        ReflectionTestUtils.setField(variableContributionStrategy, "poolLimit", 0.0);

        // When & Then
        assertThrows(ArithmeticException.class, () ->
                variableContributionStrategy.calculateContribution(betAmount, jackpot));
    }

    @Test
    void calculateContribution_ShouldHandleNegativeCurrentPool() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(-500.0));

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        // Should be higher than initial due to negative ratio
        assertTrue(result.compareTo(BigDecimal.valueOf(20.0)) > 0);
    }

    @Test
    void calculateContribution_ShouldHandleZeroBetAmount() {
        // Given
        BigDecimal zeroBetAmount = BigDecimal.ZERO;

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(zeroBetAmount, jackpot);

        // Then
        assertEquals(BigDecimal.ZERO.setScale(2), result.setScale(2));
    }

    @Test
    void calculateContribution_ShouldHandleNegativeBetAmount() {
        // Given
        BigDecimal negativeBetAmount = BigDecimal.valueOf(-100.0);
        jackpot.setCurrentPoolValue(BigDecimal.ZERO); // Use initial percentage

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(negativeBetAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(-20.0).setScale(2), result.setScale(2)); // 20% of -100
    }

    @Test
    void calculateContribution_ShouldHandleNullBetAmount() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
                variableContributionStrategy.calculateContribution(null, jackpot));
    }

    @Test
    void calculateContribution_ShouldHandleNullJackpot() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
                variableContributionStrategy.calculateContribution(betAmount, null));
    }

    @Test
    void calculateContribution_ShouldHandleNullCurrentPoolValue() {
        // Given
        jackpot.setCurrentPoolValue(null);

        // When & Then
        assertThrows(NullPointerException.class, () ->
                variableContributionStrategy.calculateContribution(betAmount, jackpot));
    }

    @Test
    void calculateContribution_ShouldHandleNullPoolLimit() {
        // Given
        ReflectionTestUtils.setField(variableContributionStrategy, "poolLimit", null);

        // When & Then
        assertThrows(NullPointerException.class, () ->
                variableContributionStrategy.calculateContribution(betAmount, jackpot));
    }

    @Test
    void calculateContribution_ShouldWorkWithDifferentConfigurations() {
        // Given
        ReflectionTestUtils.setField(variableContributionStrategy, "initialPercentage", 30.0);
        ReflectionTestUtils.setField(variableContributionStrategy, "minPercentage", 10.0);
        ReflectionTestUtils.setField(variableContributionStrategy, "poolLimit", 20000.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(15000.0));

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        // Should be: 30 - (0.75 * (30 - 10)) = 30 - 15 = 15%
        assertEquals(BigDecimal.valueOf(15.0).setScale(2), result.setScale(2));
    }

    @Test
    void calculateContribution_ShouldHandleEqualInitialAndMinPercentages() {
        // Given
        ReflectionTestUtils.setField(variableContributionStrategy, "initialPercentage", 15.0);
        ReflectionTestUtils.setField(variableContributionStrategy, "minPercentage", 15.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(20000.0));

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        assertEquals(BigDecimal.valueOf(15.0).setScale(2), result.setScale(2)); // Should always be 15%
    }

    @Test
    void calculateContribution_ShouldHandleLargeBetAmounts() {
        // Given
        BigDecimal largeBetAmount = BigDecimal.valueOf(1000000.0);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(15000.0));

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(largeBetAmount, jackpot);

        // Then
        // Should be: 20 - (1.5 * (20 - 5)) = 20 - 22.5 = -2.5, but capped at 5%
        assertEquals(BigDecimal.valueOf(50000.0).setScale(3), result.setScale(3)); // 5% of 1,000,000
    }

    @Test
    void calculateContribution_ShouldHandleSmallBetAmounts() {
        // Given
        BigDecimal smallBetAmount = BigDecimal.valueOf(0.01);
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(5000.0));

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(smallBetAmount, jackpot);

        // Then
        // Should be: 20 - (0.5 * (20 - 5)) = 20 - 7.5 = 12.5%
        assertEquals(BigDecimal.valueOf(0.00125).setScale(5), result.setScale(5)); // 12.5% of 0.01
    }

    @Test
    void calculateContribution_ShouldHandlePrecisionCorrectly() {
        // Given
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(3333.33));
        BigDecimal betAmount = BigDecimal.valueOf(300.0);

        // When
        BigDecimal result = variableContributionStrategy.calculateContribution(betAmount, jackpot);

        // Then
        // Should be: 20 - (0.33333 * (20 - 5)) = 20 - 5 = 15%
        assertEquals(BigDecimal.valueOf(45.0).setScale(2, RoundingMode.HALF_UP), result.setScale(2, RoundingMode.HALF_UP)); // 15% of 300
    }
}
