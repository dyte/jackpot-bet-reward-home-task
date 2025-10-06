package com.sporty.bet_jackpot.controller;

import com.sporty.bet_jackpot.dto.RewardResponse;
import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.model.JackpotContribution;
import com.sporty.bet_jackpot.model.JackpotReward;
import com.sporty.bet_jackpot.repository.JackpotContributionRepository;
import com.sporty.bet_jackpot.repository.JackpotRewardRepository;
import com.sporty.bet_jackpot.service.JackpotRewardService;
import com.sporty.bet_jackpot.service.JackpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JackpotClosedIntegrationTest {

    @Autowired
    private JackpotRewardService rewardService;

    @Autowired
    private JackpotContributionRepository contributionRepository;

    @Autowired
    private JackpotRewardRepository rewardRepository;

    @Autowired
    private JackpotService jackpotService;

    private Jackpot jackpot;
    private JackpotContribution contribution;

    @BeforeEach
    void setUp() {
        // Create a jackpot
        jackpot = new Jackpot();
        jackpot.setName("Test Jackpot");
        jackpot.setContributionType(ContributionType.FIXED);
        jackpot.setRewardType(RewardType.FIXED);
        jackpot.setInitialPoolValue(BigDecimal.valueOf(1000));
        jackpot.setCurrentPoolValue(BigDecimal.valueOf(500));
        jackpot.setContributionPercentage(0.1);
        jackpot.setRewardChancePercentage(0.5);
        jackpot = jackpotService.save(jackpot);

        // Create a contribution
        contribution = new JackpotContribution();
        contribution.setBetId("bet-test-closed");
        contribution.setUserId("user-test");
        contribution.setJackpotId(jackpot.getId());
        contribution.setContributionAmount(BigDecimal.valueOf(100));
        contribution.setCreatedAt(LocalDateTime.now());
        contributionRepository.save(contribution);
    }

    @Test
    void evaluateReward_ShouldReturnAlreadyWonMessage_WhenThisBetHasAlreadyWon() {
        // Given - Create an existing reward record for this specific bet
        BigDecimal rewardAmount = BigDecimal.valueOf(500);
        JackpotReward existingReward = new JackpotReward();
        existingReward.setBetId("bet-test-closed");
        existingReward.setUserId("user-test");
        existingReward.setJackpotId(jackpot.getId());
        existingReward.setJackpotRewardAmount(rewardAmount);
        existingReward.setCreatedAt(LocalDateTime.now());
        rewardRepository.save(existingReward);

        // When
        RewardResponse response = rewardService.evaluateReward("bet-test-closed");

        // Then
        assertNotNull(response);
        assertEquals("bet-test-closed", response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(rewardAmount, response.getRewardAmount());
        assertEquals("You have already won this jackpot", response.getMessage());
    }

    @Test
    void evaluateReward_ShouldReturnJackpotAlreadyWon_WhenAnotherBetHasWon() {
        // Given - Create a reward record for a different bet in the same jackpot
        JackpotReward otherBetReward = new JackpotReward();
        otherBetReward.setBetId("bet-other-winner");
        otherBetReward.setUserId("user-other");
        otherBetReward.setJackpotId(jackpot.getId());
        otherBetReward.setJackpotRewardAmount(BigDecimal.valueOf(500));
        otherBetReward.setCreatedAt(LocalDateTime.now());
        rewardRepository.save(otherBetReward);

        // When
        RewardResponse response = rewardService.evaluateReward("bet-test-closed");

        // Then
        assertNotNull(response);
        assertEquals("bet-test-closed", response.getBetId());
        assertFalse(response.isWinner());
        assertEquals(BigDecimal.ZERO, response.getRewardAmount());
        assertEquals("Jackpot has already been won", response.getMessage());
    }

    @Test
    void evaluateReward_ShouldProceedWithEvaluation_WhenNoRewardExists() {
        // When - No existing reward
        RewardResponse response = rewardService.evaluateReward("bet-test-closed");

        // Then - Should proceed with normal evaluation
        assertNotNull(response);
        assertEquals("bet-test-closed", response.getBetId());
        // The actual result depends on the strategy evaluation
        assertNotNull(response.getMessage());
    }
}
