package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.dto.BetRequest;
import com.sporty.bet_jackpot.exception.business_exceptions.InvalidBetIdExceptionJackPot;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.model.JackpotContribution;
import com.sporty.bet_jackpot.repository.JackpotContributionRepository;
import com.sporty.bet_jackpot.strategy.contribution.ContributionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JackpotContributionService {

    private final JackpotService jackpotService;
    private final JackpotContributionRepository contributionRepository;

    @CacheEvict(value = "jackpots", key = "#betRequest.jackpotId")
    public void processContribution(BetRequest betRequest) {
        // Check if bet ID already exists
        contributionRepository.findByBetId(betRequest.getBetId())
            .ifPresent(existingContribution -> {
                throw new InvalidBetIdExceptionJackPot(betRequest.getBetId());
            });

        // Find jackpot
        Jackpot jackpot = jackpotService.findById(betRequest.getJackpotId());

        // Get appropriate strategy
        ContributionStrategy strategy = jackpotService.getContributionStrategy(
            jackpot.getContributionType()
        );

        // Calculate contribution
        BigDecimal contributionAmount = strategy.calculateContribution(
            betRequest.getBetAmount(), 
            jackpot
        );

        // Update jackpot pool
        jackpot.setCurrentPoolValue(
            jackpot.getCurrentPoolValue().add(contributionAmount)
        );


        jackpotService.save(jackpot);

        // Save contribution record
        JackpotContribution contribution = new JackpotContribution();
        contribution.setBetId(betRequest.getBetId());
        contribution.setUserId(betRequest.getUserId());
        contribution.setJackpotId(betRequest.getJackpotId());
        contribution.setStakeAmount(betRequest.getBetAmount());
        contribution.setContributionAmount(contributionAmount);
        contribution.setCurrentJackpotAmount(jackpot.getCurrentPoolValue());
        contribution.setCreatedAt(LocalDateTime.now());

        contributionRepository.save(contribution);

        log.info("Processed contribution for bet: {}, amount: {}", 
            betRequest.getBetId(), contributionAmount);
    }
}
