package com.sporty.bet_jackpot.builder;

import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.model.Jackpot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JackpotBuilderTest {

    @Test
    void build_ShouldCreateValidJackpot_WhenAllRequiredFieldsProvided() {
        // When
        Jackpot jackpot = JackpotBuilder.newJackpot("Test Jackpot")
                .withFixedContribution()
                .withFixedReward()
                .withPoolValue(BigDecimal.valueOf(1000))
                .withContributionPercentage(5.0)
                .withRewardChancePercentage(1.0)
                .withCurrentTimestamp()
                .build();

        // Then
        assertNotNull(jackpot);
        assertEquals("Test Jackpot", jackpot.getName());
        assertEquals(ContributionType.FIXED, jackpot.getContributionType());
        assertEquals(RewardType.FIXED, jackpot.getRewardType());
        assertEquals(BigDecimal.valueOf(1000), jackpot.getInitialPoolValue());
        assertEquals(BigDecimal.valueOf(1000), jackpot.getCurrentPoolValue());
        assertEquals(5.0, jackpot.getContributionPercentage());
        assertEquals(1.0, jackpot.getRewardChancePercentage());
        assertNotNull(jackpot.getCreatedAt());
        assertNotNull(jackpot.getUpdatedAt());
    }

    @Test
    void build_ShouldCreateValidJackpot_WhenUsingConvenienceMethods() {
        // When
        Jackpot jackpot = JackpotBuilder.newJackpot("Progressive Jackpot")
                .withVariableContribution()
                .withVariableReward()
                .withPoolValue(2000.0)
                .withCurrentTimestamp()
                .build();

        // Then
        assertNotNull(jackpot);
        assertEquals("Progressive Jackpot", jackpot.getName());
        assertEquals(ContributionType.VARIABLE, jackpot.getContributionType());
        assertEquals(RewardType.VARIABLE, jackpot.getRewardType());
        assertEquals(BigDecimal.valueOf(2000.0), jackpot.getInitialPoolValue());
        assertEquals(BigDecimal.valueOf(2000.0), jackpot.getCurrentPoolValue());
    }

    @Test
    void build_ShouldThrowException_WhenNameIsMissing() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot()
                        .withFixedContribution()
                        .withFixedReward()
                        .withPoolValue(BigDecimal.valueOf(1000))
                        .build()
        );

        assertEquals("Jackpot name is required", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenContributionTypeIsMissing() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot("Test Jackpot")
                        .withFixedReward()
                        .withPoolValue(BigDecimal.valueOf(1000))
                        .build()
        );

        assertEquals("Contribution type is required", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenRewardTypeIsMissing() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot("Test Jackpot")
                        .withFixedContribution()
                        .withPoolValue(BigDecimal.valueOf(1000))
                        .build()
        );

        assertEquals("Reward type is required", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenInitialPoolValueIsInvalid() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot("Test Jackpot")
                        .withFixedContribution()
                        .withFixedReward()
                        .withInitialPoolValue(BigDecimal.ZERO)
                        .withCurrentPoolValue(BigDecimal.valueOf(1000))
                        .build()
        );

        assertEquals("Initial pool value must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenCurrentPoolValueIsInvalid() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot("Test Jackpot")
                        .withFixedContribution()
                        .withFixedReward()
                        .withInitialPoolValue(BigDecimal.valueOf(1000))
                        .withCurrentPoolValue(BigDecimal.ZERO)
                        .build()
        );

        assertEquals("Current pool value must be positive", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenContributionPercentageIsInvalid() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot("Test Jackpot")
                        .withFixedContribution()
                        .withFixedReward()
                        .withPoolValue(BigDecimal.valueOf(1000))
                        .withContributionPercentage(150.0) // Invalid: > 100
                        .build()
        );

        assertEquals("Contribution percentage must be between 0 and 100", exception.getMessage());
    }

    @Test
    void build_ShouldThrowException_WhenRewardChancePercentageIsInvalid() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                JackpotBuilder.newJackpot("Test Jackpot")
                        .withFixedContribution()
                        .withFixedReward()
                        .withPoolValue(BigDecimal.valueOf(1000))
                        .withRewardChancePercentage(-5.0) // Invalid: < 0
                        .build()
        );

        assertEquals("Reward chance percentage must be between 0 and 100", exception.getMessage());
    }

    @Test
    void build_ShouldCreateJackpot_WhenOptionalFieldsAreNull() {
        // When
        Jackpot jackpot = JackpotBuilder.newJackpot("Test Jackpot")
                .withFixedContribution()
                .withFixedReward()
                .withPoolValue(BigDecimal.valueOf(1000))
                .build();

        // Then
        assertNotNull(jackpot);
        assertNull(jackpot.getContributionPercentage());
        assertNull(jackpot.getRewardChancePercentage());
    }

    @Test
    void build_ShouldSetSameTimestamp_WhenUsingWithCurrentTimestamp() {
        // When
        LocalDateTime beforeBuild = LocalDateTime.now();
        Jackpot jackpot = JackpotBuilder.newJackpot("Test Jackpot")
                .withFixedContribution()
                .withFixedReward()
                .withPoolValue(BigDecimal.valueOf(1000))
                .withCurrentTimestamp()
                .build();
        LocalDateTime afterBuild = LocalDateTime.now();

        // Then
        assertNotNull(jackpot.getCreatedAt());
        assertNotNull(jackpot.getUpdatedAt());
        assertEquals(jackpot.getCreatedAt(), jackpot.getUpdatedAt());
        assertTrue(jackpot.getCreatedAt().isAfter(beforeBuild.minusSeconds(1)));
        assertTrue(jackpot.getCreatedAt().isBefore(afterBuild.plusSeconds(1)));
    }
}
