package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.RewardResponse;
import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.exception.business_exceptions.BetNotFoundException;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.model.JackpotContribution;
import com.sporty.bet_jackpot.model.JackpotReward;
import com.sporty.bet_jackpot.repository.JackpotContributionRepository;
import com.sporty.bet_jackpot.repository.JackpotRewardRepository;
import com.sporty.bet_jackpot.strategy.reward.RewardStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JackpotRewardServiceTest {

    @Mock
    private JackpotService jackpotService;

    @Mock
    private JackpotContributionRepository contributionRepository;

    @Mock
    private JackpotRewardRepository rewardRepository;

    @Mock
    private RewardStrategy rewardStrategy;

    private JackpotRewardService rewardService;

    @BeforeEach
    void setUp() {
        rewardService = new JackpotRewardService(jackpotService, contributionRepository, rewardRepository);
    }

    @Test
    void evaluateReward_ShouldReturnWinnerResponse_WhenBetWins() {
        // Given
        String betId = "bet-123";
        JackpotContribution contribution = createTestContribution(betId);
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(new BigDecimal("5000.00"));
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getRewardStrategy(jackpot.getRewardType())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(jackpot)).thenReturn(true);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(rewardRepository.save(any(JackpotReward.class))).thenReturn(new JackpotReward());

        // When
        RewardResponse response = rewardService.evaluateReward(betId);

        // Then
        assertNotNull(response);
        assertEquals(betId, response.getBetId());
        assertTrue(response.isWinner());
        assertEquals(new BigDecimal("5000.00"), response.getRewardAmount());
        assertEquals("Congratulations! You won the jackpot!", response.getMessage());
        
        verify(contributionRepository).findByBetId(betId);
        verify(jackpotService).findById(contribution.getJackpotId());
        verify(jackpotService).getRewardStrategy(jackpot.getRewardType());
        verify(rewardStrategy).evaluateWin(jackpot);
        verify(rewardRepository).save(any(JackpotReward.class));
        verify(jackpotService).save(any(Jackpot.class));
    }

    @Test
    void evaluateReward_ShouldReturnLoserResponse_WhenBetLoses() {
        // Given
        String betId = "bet-456";
        JackpotContribution contribution = createTestContribution(betId);
        Jackpot jackpot = createTestJackpot(1L);
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getRewardStrategy(jackpot.getRewardType())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(jackpot)).thenReturn(false);

        // When
        RewardResponse response = rewardService.evaluateReward(betId);

        // Then
        assertNotNull(response);
        assertEquals(betId, response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(BigDecimal.ZERO, response.getRewardAmount());
        assertEquals("Better luck next time!", response.getMessage());
        
        verify(contributionRepository).findByBetId(betId);
        verify(jackpotService).findById(contribution.getJackpotId());
        verify(jackpotService).getRewardStrategy(jackpot.getRewardType());
        verify(rewardStrategy).evaluateWin(jackpot);
        verify(rewardRepository, never()).save(any());
        verify(jackpotService, never()).save(any());
    }

    @Test
    void evaluateReward_ShouldThrowException_WhenBetNotFound() {
        // Given
        String betId = "non-existent-bet";
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.empty());

        // When & Then
        BetNotFoundException exception = assertThrows(
                BetNotFoundException.class,
                () -> rewardService.evaluateReward(betId)
        );

        assertTrue(exception.getMessage().contains("Bet not found"));
        verify(contributionRepository).findByBetId(betId);
        verify(jackpotService, never()).findById(any());
    }

    @Test
    void evaluateReward_ShouldCreateRewardRecord_WhenWinner() {
        // Given
        String betId = "bet-789";
        JackpotContribution contribution = createTestContribution(betId);
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(new BigDecimal("3000.00"));
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getRewardStrategy(jackpot.getRewardType())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(jackpot)).thenReturn(true);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(rewardRepository.save(any(JackpotReward.class))).thenReturn(new JackpotReward());

        // When
        rewardService.evaluateReward(betId);

        // Then
        ArgumentCaptor<JackpotReward> rewardCaptor = ArgumentCaptor.forClass(JackpotReward.class);
        verify(rewardRepository).save(rewardCaptor.capture());
        
        JackpotReward savedReward = rewardCaptor.getValue();
        assertEquals(betId, savedReward.getBetId());
        assertEquals(contribution.getUserId(), savedReward.getUserId());
        assertEquals(contribution.getJackpotId(), savedReward.getJackpotId());
        assertEquals(new BigDecimal("3000.00"), savedReward.getJackpotRewardAmount());
        assertNotNull(savedReward.getCreatedAt());
    }

    @Test
    void evaluateReward_ShouldResetJackpotToInitialValue_WhenWinner() {
        // Given
        String betId = "bet-winner";
        JackpotContribution contribution = createTestContribution(betId);
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setInitialPoolValue(new BigDecimal("1000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("5000.00"));
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getRewardStrategy(jackpot.getRewardType())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(jackpot)).thenReturn(true);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(rewardRepository.save(any(JackpotReward.class))).thenReturn(new JackpotReward());

        // When
        rewardService.evaluateReward(betId);

        // Then
        ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
        verify(jackpotService).save(jackpotCaptor.capture());
        
        Jackpot savedJackpot = jackpotCaptor.getValue();
        assertEquals(new BigDecimal("1000.00"), savedJackpot.getCurrentPoolValue());
    }

    @Test
    void evaluateReward_ShouldHandleNullBetId() {
        // Given
        String betId = null;
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.empty());

        // When & Then
        BetNotFoundException exception = assertThrows(
                BetNotFoundException.class,
                () -> rewardService.evaluateReward(betId)
        );

        assertTrue(exception.getMessage().contains("Bet not found"));
    }

    @Test
    void evaluateReward_ShouldHandleEmptyBetId() {
        // Given
        String betId = "";
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.empty());

        // When & Then
        BetNotFoundException exception = assertThrows(
                BetNotFoundException.class,
                () -> rewardService.evaluateReward(betId)
        );

        assertTrue(exception.getMessage().contains("Bet not found"));
    }

    @Test
    void evaluateReward_ShouldHandleStrategyEvaluationError() {
        // Given
        String betId = "bet-error";
        JackpotContribution contribution = createTestContribution(betId);
        Jackpot jackpot = createTestJackpot(1L);
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getRewardStrategy(jackpot.getRewardType())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(jackpot)).thenThrow(new RuntimeException("Strategy evaluation failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> rewardService.evaluateReward(betId));
        
        verify(contributionRepository).findByBetId(betId);
        verify(jackpotService).findById(contribution.getJackpotId());
        verify(rewardStrategy).evaluateWin(jackpot);
        verify(rewardRepository, never()).save(any());
    }

    @Test
    void evaluateReward_ShouldHandleJackpotNotFound() {
        // Given
        String betId = "bet-jackpot-not-found";
        JackpotContribution contribution = createTestContribution(betId);
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenThrow(new RuntimeException("Jackpot not found"));

        // When & Then
        assertThrows(RuntimeException.class, () -> rewardService.evaluateReward(betId));
        
        verify(contributionRepository).findByBetId(betId);
        verify(jackpotService).findById(contribution.getJackpotId());
        verify(rewardRepository, never()).save(any());
    }

    @Test
    void evaluateReward_ShouldHandleZeroRewardAmount() {
        // Given
        String betId = "bet-zero-reward";
        JackpotContribution contribution = createTestContribution(betId);
        Jackpot jackpot = createTestJackpot(1L);
        jackpot.setCurrentPoolValue(BigDecimal.ZERO);
        
        when(contributionRepository.findByBetId(betId)).thenReturn(Optional.of(contribution));
        when(jackpotService.findById(contribution.getJackpotId())).thenReturn(jackpot);
        when(jackpotService.getRewardStrategy(jackpot.getRewardType())).thenReturn(rewardStrategy);
        when(rewardStrategy.evaluateWin(jackpot)).thenReturn(true);
        when(jackpotService.save(any(Jackpot.class))).thenReturn(jackpot);
        when(rewardRepository.save(any(JackpotReward.class))).thenReturn(new JackpotReward());

        // When
        RewardResponse response = rewardService.evaluateReward(betId);

        // Then
        assertNotNull(response);
        assertTrue(response.isWinner());
        assertEquals(BigDecimal.ZERO, response.getRewardAmount());
        verify(rewardRepository).save(any(JackpotReward.class));
    }

    private JackpotContribution createTestContribution(String betId) {
        JackpotContribution contribution = new JackpotContribution();
        contribution.setBetId(betId);
        contribution.setUserId("user-123");
        contribution.setJackpotId(1L);
        contribution.setStakeAmount(new BigDecimal("100.00"));
        contribution.setContributionAmount(new BigDecimal("10.00"));
        contribution.setCurrentJackpotAmount(new BigDecimal("1000.00"));
        contribution.setCreatedAt(LocalDateTime.now());
        return contribution;
    }

    private Jackpot createTestJackpot(Long id) {
        Jackpot jackpot = new Jackpot();
        jackpot.setId(id);
        jackpot.setName("Test Jackpot");
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setRewardType(RewardType.VARIABLE);
        jackpot.setInitialPoolValue(new BigDecimal("1000.00"));
        jackpot.setCurrentPoolValue(new BigDecimal("1000.00"));
        jackpot.setContributionPercentage(10.0);
        jackpot.setRewardChancePercentage(5.0);
        jackpot.setCreatedAt(LocalDateTime.now());
        jackpot.setUpdatedAt(LocalDateTime.now());
        return jackpot;
    }
}
