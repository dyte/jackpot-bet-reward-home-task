package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.RewardResponse;
import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.model.JackpotContribution;
import com.sporty.bet_jackpot.model.JackpotReward;
import com.sporty.bet_jackpot.repository.JackpotContributionRepository;
import com.sporty.bet_jackpot.repository.JackpotRewardRepository;
import com.sporty.bet_jackpot.strategy.reward.RewardStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JackpotRewardServiceClosedTest {

    @Mock
    private JackpotService jackpotService;

    @Mock
    private JackpotContributionRepository contributionRepository;

    @Mock
    private JackpotRewardRepository rewardRepository;

    @Mock
    private RewardStrategy rewardStrategy;

    @InjectMocks
    private JackpotRewardService rewardService;

    private Jackpot jackpot;
    private JackpotContribution contribution;
    private JackpotReward existingReward;

    @BeforeEach
    void setUp() {
        // Create a jackpot
        jackpot = new Jackpot(1L, "Test Jackpot", ContributionType.FIXED, RewardType.FIXED,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(500), 0.1, 0.5,
                LocalDateTime.now(), LocalDateTime.now());

        // Create a contribution
        contribution = new JackpotContribution();
        contribution.setBetId("bet-123");
        contribution.setUserId("user-123");
        contribution.setJackpotId(1L);
        contribution.setContributionAmount(BigDecimal.valueOf(100));
        contribution.setCreatedAt(LocalDateTime.now());

        // Create an existing reward
        existingReward = new JackpotReward();
        existingReward.setBetId("bet-123");
        existingReward.setUserId("user-123");
        existingReward.setJackpotId(1L);
        existingReward.setJackpotRewardAmount(BigDecimal.valueOf(500));
        existingReward.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void evaluateReward_ShouldReturnAlreadyWonMessage_WhenThisBetHasAlreadyWon() {
        // Given
        when(contributionRepository.findByBetId("bet-123")).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(1L)).thenReturn(jackpot);
        when(rewardRepository.findByBetIdAndJackpotId("bet-123", 1L)).thenReturn(Optional.of(existingReward));

        // When
        RewardResponse response = rewardService.evaluateReward("bet-123");

        // Then
        assertNotNull(response);
        assertEquals("bet-123", response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(existingReward.getJackpotRewardAmount(), response.getRewardAmount());
        assertEquals("You have already won this jackpot", response.getMessage());
    }

    @Test
    void evaluateReward_ShouldReturnJackpotAlreadyWon_WhenAnotherBetHasWon() {
        // Given
        when(contributionRepository.findByBetId("bet-123")).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(1L)).thenReturn(jackpot);
        when(rewardRepository.findByBetIdAndJackpotId("bet-123", 1L)).thenReturn(Optional.empty());
        when(rewardRepository.existsByJackpotId(1L)).thenReturn(true);

        // When
        RewardResponse response = rewardService.evaluateReward("bet-123");

        // Then
        assertNotNull(response);
        assertEquals("bet-123", response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(BigDecimal.ZERO, response.getRewardAmount());
        assertEquals("Jackpot has already been won", response.getMessage());
    }

    @Test
    void evaluateReward_ShouldProceedWithEvaluation_WhenNoRewardExists() {
        // Given
        when(contributionRepository.findByBetId("bet-123")).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(1L)).thenReturn(jackpot);
        when(rewardRepository.findByBetIdAndJackpotId("bet-123", 1L)).thenReturn(Optional.empty());
        when(rewardRepository.existsByJackpotId(1L)).thenReturn(false);
        when(jackpotService.getRewardStrategy(any())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(any())).thenReturn(false);

        // When
        RewardResponse response = rewardService.evaluateReward("bet-123");

        // Then
        assertNotNull(response);
        assertEquals("bet-123", response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(BigDecimal.ZERO, response.getRewardAmount());
        assertEquals("Better luck next time!", response.getMessage());
    }

    @Test
    void evaluateReward_ShouldProceedWithEvaluation_WhenRewardExistsForDifferentBet() {
        // Given
        when(contributionRepository.findByBetId("bet-123")).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(1L)).thenReturn(jackpot);
        when(rewardRepository.findByBetIdAndJackpotId("bet-123", 1L)).thenReturn(Optional.empty());
        when(rewardRepository.existsByJackpotId(1L)).thenReturn(false);
        when(jackpotService.getRewardStrategy(any())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(any())).thenReturn(false);

        // When
        RewardResponse response = rewardService.evaluateReward("bet-123");

        // Then
        assertNotNull(response);
        assertEquals("bet-123", response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(BigDecimal.ZERO, response.getRewardAmount());
        assertEquals("Better luck next time!", response.getMessage());
    }
}
