package com.sporty.bet_jackpot.validation;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.service.JackpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BetRequestValidatorTest {

    @Mock
    private JackpotService jackpotService;

    private BetRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BetRequestValidator(jackpotService);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenValidRequest() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 1L, BigDecimal.valueOf(100.0));
        Jackpot jackpot = createFixedJackpot();

        when(jackpotService.findById(1L)).thenReturn(jackpot);

        // When
        boolean result = validator.isValid(betRequest, null);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenJackpotNotFound() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 999L, BigDecimal.valueOf(100.0));

        when(jackpotService.findById(999L)).thenThrow(new RuntimeException("Jackpot not found"));

        // When
        boolean result = validator.isValid(betRequest, null);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBetAmountBelowMinimum() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 1L, BigDecimal.valueOf(0.50)); // Below $1 minimum
        Jackpot jackpot = createFixedJackpot();

        when(jackpotService.findById(1L)).thenReturn(jackpot);

        // When
        boolean result = validator.isValid(betRequest, null);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenBetAmountMeetsMinimumForFixedJackpot() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 1L, BigDecimal.valueOf(1.00)); // Exactly $1 minimum
        Jackpot jackpot = createFixedJackpot();

        when(jackpotService.findById(1L)).thenReturn(jackpot);

        // When
        boolean result = validator.isValid(betRequest, null);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenBetAmountMeetsMinimumForVariableJackpot() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 2L, BigDecimal.valueOf(5.00)); // Exactly $5 minimum
        Jackpot jackpot = createVariableJackpot();

        when(jackpotService.findById(2L)).thenReturn(jackpot);

        // When
        boolean result = validator.isValid(betRequest, null);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_ShouldReturnFalse_WhenBetAmountBelowMinimumForVariableJackpot() {
        // Given
        BetRequest betRequest = new BetRequest("bet-123", "user-456", 2L, BigDecimal.valueOf(4.00)); // Below $5 minimum
        Jackpot jackpot = createVariableJackpot();

        when(jackpotService.findById(2L)).thenReturn(jackpot);

        // When
        boolean result = validator.isValid(betRequest, null);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenRequestIsNull() {
        // When
        boolean result = validator.isValid(null, null);

        // Then
        assertTrue(result); // Should return true for null (let @NotNull handle it)
    }

    private Jackpot createFixedJackpot() {
        Jackpot jackpot = new Jackpot();
        jackpot.setId(1L);
        jackpot.setName("Fixed Jackpot");
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setRewardType(RewardType.FIXED);
        jackpot.setInitialPoolValue(BigDecimal.valueOf(1000));
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(1000));
        jackpot.setCreatedAt(LocalDateTime.now());
        return jackpot;
    }

    private Jackpot createVariableJackpot() {
        Jackpot jackpot = new Jackpot();
        jackpot.setId(2L);
        jackpot.setName("Variable Jackpot");
        jackpot.setContributionType(ContributionType.VARIABLE);
        jackpot.setRewardType(RewardType.VARIABLE);
        jackpot.setInitialPoolValue(BigDecimal.valueOf(1000));
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(1000));
        jackpot.setCreatedAt(LocalDateTime.now());
        return jackpot;
    }
}
