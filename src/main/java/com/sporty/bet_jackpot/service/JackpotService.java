package com.sporty.bet_jackpot.service;

import com.sporty.bet_jackpot.enums.ContributionType;
import com.sporty.bet_jackpot.enums.RewardType;
import com.sporty.bet_jackpot.exception.business_exceptions.JackpotNotFoundException;
import com.sporty.bet_jackpot.model.Jackpot;
import com.sporty.bet_jackpot.repository.JackpotRepository;
import com.sporty.bet_jackpot.strategy.contribution.ContributionStrategy;
import com.sporty.bet_jackpot.strategy.contribution.FixedContributionStrategy;
import com.sporty.bet_jackpot.strategy.contribution.VariableContributionStrategy;
import com.sporty.bet_jackpot.strategy.reward.FixedRewardStrategy;
import com.sporty.bet_jackpot.strategy.reward.RewardStrategy;
import com.sporty.bet_jackpot.strategy.reward.VariableRewardStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JackpotService {
    
    private final JackpotRepository jackpotRepository;
    private final FixedContributionStrategy fixedContributionStrategy;
    private final VariableContributionStrategy variableContributionStrategy;
    private final FixedRewardStrategy fixedRewardStrategy;
    private final VariableRewardStrategy variableRewardStrategy;
    
    public ContributionStrategy getContributionStrategy(ContributionType type) {
        return switch (type) {
            case FIXED -> fixedContributionStrategy;
            case VARIABLE -> variableContributionStrategy;
        };
    }
    
    public RewardStrategy getRewardStrategy(RewardType type) {
        return switch (type) {
            case FIXED -> fixedRewardStrategy;
            case VARIABLE -> variableRewardStrategy;
        };
    }
    
    @Cacheable(value = "jackpots", key = "#id")
    public Jackpot findById(Long id) {
        log.debug("Fetching jackpot from database: {}", id);
        return jackpotRepository.findById(id)
            .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found: " + id));
    }
    
    @CacheEvict(value = "jackpots", key = "#jackpot.id")
    public Jackpot save(Jackpot jackpot) {
        log.debug("Saving jackpot and evicting cache: {}", jackpot.getId());
        return jackpotRepository.save(jackpot);
    }
    
    @CacheEvict(value = "jackpots", key = "#id")
    public void evictCache(Long id) {
        log.debug("Evicting cache for jackpot: {}", id);
    }
}